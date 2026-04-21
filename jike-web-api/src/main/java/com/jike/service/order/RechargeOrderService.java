package com.jike.service.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jike.controller.order.request.CreateRechargeOrderRequest;
import com.jike.mapper.order.RechargeOrderMapper;
import com.jike.mapper.score.PointsLedgerMapper;
import com.jike.model.order.RechargeOrderDTO;
import com.jike.model.order.RechargeOrderEntity;
import com.jike.model.order.RechargeOrderVO;
import com.jike.model.score.PointsLedgerEntity;
import com.jike.vendor.WechatPayV3Client;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 积分充值订单 Service
 */
@Service
@Slf4j
public class RechargeOrderService extends AppBaseServiceV2<RechargeOrderEntity, RechargeOrderVO, RechargeOrderDTO> {

    private final RechargeOrderMapper mapper;
    private final PointsLedgerMapper pointsLedgerMapper;
    private final WechatPayV3Client wechatPayV3Client;

    /**
     * 充值套餐配置
     */
    private static final Map<String, int[]> RECHARGE_PACKAGES = new LinkedHashMap<>();

    static {
        RECHARGE_PACKAGES.put("pkg_500", new int[]{500, 1});
        RECHARGE_PACKAGES.put("pkg_2000", new int[]{2000, 1});
        RECHARGE_PACKAGES.put("pkg_5000", new int[]{5000, 1});
        RECHARGE_PACKAGES.put("pkg_12000", new int[]{12000, 1});
    }

    public RechargeOrderService(RechargeOrderMapper mapper, PointsLedgerMapper pointsLedgerMapper, WechatPayV3Client wechatPayV3Client) {
        this.mapper = mapper;
        this.pointsLedgerMapper = pointsLedgerMapper;
        this.wechatPayV3Client = wechatPayV3Client;
    }

    public Class<RechargeOrderVO> getVoClass() {
        return RechargeOrderVO.class;
    }

    public Class<RechargeOrderEntity> getEntityClass() {
        return RechargeOrderEntity.class;
    }

    @Override
    public RechargeOrderMapper getMapper() {
        return mapper;
    }

    /**
     * 创建充值订单
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse createOrder(CreateRechargeOrderRequest request) {
        // 1. 检查套餐是否有效
        int[] packageConfig = RECHARGE_PACKAGES.get(request.getPackageId());
        if (packageConfig == null) {
            return ApiResponseUtil.getApiResponseParamsError("套餐不存在");
        }

        int points = packageConfig[0];
        int amountFen = packageConfig[1];

        // 2. 生成订单号
        String orderId = buildOutTradeNo();

        // 3. 创建订单记录
        RechargeOrderEntity order = new RechargeOrderEntity();
        order.setId(orderId);
        order.setUserId(request.getUserId());
        order.setPackageId(request.getPackageId());
        order.setPoints(points);
        order.setAmountFen(amountFen);
        order.setStatus("CREATED");
        order.setCreateTime(System.currentTimeMillis());
        order.setUpdateTime(System.currentTimeMillis());

        mapper.insert(order);

        // 4. 调用微信支付创建Native订单
        String attachJson = JSON.toJSONString(new JSONObject() {{
            put("type", "RECHARGE_POINTS");
            put("userId", request.getUserId());
            put("packageId", request.getPackageId());
            put("points", points);
        }});

        WechatPayV3Client.NativeOrderResult nativeResult = wechatPayV3Client.createNativeOrder(
                "积分充值 " + points,
                amountFen,
                orderId,
                attachJson
        );

        if (!nativeResult.isSuccess()) {
            log.error("创建微信支付订单失败: {}", nativeResult.getError());
            return ApiResponseUtil.getApiResponseFailure("创建支付订单失败");
        }

        // 5. 更新二维码链接
        mapper.updateCodeUrl(orderId, nativeResult.getCodeUrl(), System.currentTimeMillis());

        // 6. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("codeUrl", nativeResult.getCodeUrl());
        result.put("points", points);
        result.put("amountFen", amountFen);

        return ApiResponseUtil.getApiResponseSuccess(result);
    }

    /**
     * 查询订单状态
     */
    public ApiResponse getOrderStatus(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            return ApiResponseUtil.getApiResponseParamsError("orderId不能为空");
        }

        RechargeOrderEntity order = mapper.selectByOrderId(orderId);
        if (order == null) {
            return ApiResponseUtil.getApiResponseFailure("订单不存在");
        }

        // 强制查询微信支付状态，确保状态同步
        WechatPayV3Client.QueryOrderResult queryResult = wechatPayV3Client.queryOrderByOutTradeNo(orderId);
        String tradeState = queryResult.getTradeState() != null ? queryResult.getTradeState() : queryResult.getError();

        // 映射订单状态
        String mappedStatus = mapTradeStateToOrderStatus(tradeState);

        // 如果状态有变化，更新数据库
        if (!mappedStatus.equals(order.getStatus())) {
            Long paidTime = "PAID".equals(mappedStatus) ? System.currentTimeMillis() : null;
            mapper.updateStatus(orderId, mappedStatus, System.currentTimeMillis(), paidTime);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", mappedStatus);
        result.put("tradeState", tradeState);

        return ApiResponseUtil.getApiResponseSuccess(result);
    }

    /**
     * 处理微信支付回调
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePayNotify(String outTradeNo, String tradeState, String userId,
                                 String packageId, int points) {
        RechargeOrderEntity order = mapper.selectByOrderId(outTradeNo);
        if (order == null) {
            log.warn("微信支付回调订单不存在: {}", outTradeNo);
            return;
        }

        if ("SUCCESS".equals(tradeState)) {
            // 检查是否已处理
            PointsLedgerEntity existingLedger = pointsLedgerMapper.selectByOrderId(outTradeNo);
            if (existingLedger == null) {
                // 写入积分账本
                PointsLedgerEntity ledger = new PointsLedgerEntity();
                ledger.setId(UUID.randomUUID().toString().replace("-", ""));
                ledger.setUserId(userId);
                ledger.setOrderId(outTradeNo);
                ledger.setPoints(points);
                ledger.setCreateTime(System.currentTimeMillis());
                pointsLedgerMapper.insert(ledger);
            }

            // 更新订单状态为已支付
            mapper.updateStatus(outTradeNo, "PAID", System.currentTimeMillis(), System.currentTimeMillis());
        } else {
            // 更新为对应状态
            String mappedStatus = mapTradeStateToOrderStatus(tradeState);
            mapper.updateStatus(outTradeNo, mappedStatus, System.currentTimeMillis(), null);
        }
    }

    /**
     * 生成订单号 (格式: yyyyMMddHHmmss + 8位随机)
     */
    private String buildOutTradeNo() {
        StringBuilder sb = new StringBuilder();
        Calendar now = Calendar.getInstance();
        sb.append(now.get(Calendar.YEAR));
        sb.append(String.format("%02d", now.get(Calendar.MONTH) + 1));
        sb.append(String.format("%02d", now.get(Calendar.DAY_OF_MONTH)));
        sb.append(String.format("%02d", now.get(Calendar.HOUR_OF_DAY)));
        sb.append(String.format("%02d", now.get(Calendar.MINUTE)));
        sb.append(String.format("%02d", now.get(Calendar.SECOND)));

        String uuid = UUID.randomUUID().toString().replace("-", "");
        sb.append(uuid.substring(0, 8));
        return sb.toString();
    }

    /**
     * 将微信交易状态映射为订单状态
     */
    private String mapTradeStateToOrderStatus(String tradeState) {
        if (tradeState == null || tradeState.isEmpty()) return "CREATED";
        return switch (tradeState) {
            case "SUCCESS", "REFUND" -> "PAID";
            case "NOTPAY", "USERPAYING", "PAYERROR" -> "CREATED";
            case "CLOSED", "REVOKED" -> "CLOSED";
            default -> "CREATED";
        };
    }
}