package com.jike.service.order;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.controller.order.request.CreateOrderRequest;
import com.jike.mapper.user.UserVipMapper;
import com.jike.model.order.GoodsEntity;
import com.jike.model.user.UserLoginEntity;
import com.jike.model.user.UserVO;
import com.jike.model.user.UserVipEntity;
import com.jike.service.score.UserScoreService;
import com.jike.service.user.UserLoginService;
import com.jike.service.user.UserService;
import com.jike.service.user.UserVipService;
import com.jike.utils.SeqNoUtil;
import com.jike.vendor.WeiXinUtil;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.order.OrderMapper;
import com.jike.model.order.OrderEntity;
import com.jike.model.order.OrderVO;
import com.jike.model.order.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
public class OrderService extends AppBaseServiceV2<OrderEntity, OrderVO,OrderDTO> {

    final OrderMapper mapper;
    public OrderService(OrderMapper mapper) { this.mapper = mapper; } 
    public Class<OrderVO> getVoClass() { return OrderVO.class; }
    public Class<OrderEntity> getEntityClass() { return OrderEntity.class; } 

    @Override
    public OrderMapper getMapper() {
        return mapper;
    }

    @Autowired
    UserLoginService userLoginService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    WeiXinUtil weiXinUtil;
    @Autowired
    UserScoreService userScoreService;
    @Autowired
    UserService userService;
    @Autowired
    UserVipService userVipService;

    /**
     * 创建订单
     */
    public ApiResponse submit(CreateOrderRequest request, String userId) throws Exception {

        // 1. 检查goodsId是否正确
        GoodsEntity goods = goodsService.getById(request.getGoodsId());
        if (goods == null) {
            return ApiResponseUtil.getApiResponseParamsError("goodsId无效");
        }

        if (goods.getType() == 1) {
            UserVipEntity userVip = userVipService.getUserVip(userId);
            if (userVip != null) {
                if (goods.getGoodsTag().compareTo(userVip.getVip()) == 0) {
                    return ApiResponseUtil.getApiResponseParamsError("你已拥有当前会员，无需重复购买");
                } else if (goods.getGoodsTag().compareTo(userVip.getVip()) < 0) {
                    return ApiResponseUtil.getApiResponseParamsError("你已拥有更高级别会员");
                }
            }
        }

        // 2. 查询openId
        UserLoginEntity userLoginEntity = userLoginService.getOne(new QueryWrapper<UserLoginEntity>().lambda()
                .eq(UserLoginEntity::getUserId, userId));
        if (userLoginEntity == null) {
            return ApiResponseUtil.getApiResponseParamsError("userId无效");
        }

        // 2. 微信统一下单
        String orderNo = SeqNoUtil.generateOrderSeqNo();
        WeiXinUtil.CreateOrderResult createOrderResult = weiXinUtil.jsApiService.createOrder(goods.getName(), (int) (goods.getPrice() * 100), orderNo, userLoginEntity.getInfo());
        if (!createOrderResult.getSuccess()) {
            return ApiResponseUtil.getApiResponseParamsError(createOrderResult.getError());
        }

        // 3. 入库
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        order.setName(goods.getName());
        order.setSign(createOrderResult.getSign());
        order.setGoodsId(goods.getId());
        order.setGoodsPrice(goods.getPrice());
        order.setUserId(Long.parseLong(userId));
        mapper.insert(order);

        // 4. 返回结果
        return ApiResponseUtil.getApiResponseSuccess(new HashMap() {{
            put("orderId", order.getId());
            put("orderNo", order.getOrderNo());
            put("sign", order.getSign());
        }});
    }

    /**
     * 校验订单
     */
    public ApiResponse checkOrder(String orderNo) throws Exception {
        OrderEntity order = getOne(new QueryWrapper<OrderEntity>().lambda().eq(OrderEntity::getOrderNo, orderNo));
        if (order == null) {
            return ApiResponseUtil.getApiResponseParamsError("orderNo不存在");
        }

        WeiXinUtil.CheckOrderResult checkOrderResult = weiXinUtil.jsApiService.checkOrder(orderNo);
        if (checkOrderResult.isPayed()) {
            order.setStatus(1); // 修改为1表示已支付，与OrderEntity注释一致
            order.setOutTradeNo(checkOrderResult.getTranscationId());
            order.setPayPrice((float) (checkOrderResult.getPrice()));
            if (!order.isAssigned()) {
                // 没下发物料，则下发物料
                if (assignForOrder(order)) {
                    order.setAssigned(true);
                    order.setPayTime(System.currentTimeMillis() / 1000);
                }
            }
            updateById(order);
        } else {
            return ApiResponseUtil.getApiResponseParamsError("订单没有支付");
        }

        return ApiResponseUtil.getApiResponseSuccess();
    }

    /**
     * 订单退款
     */
    public ApiResponse refundOrder(String orderNo) throws Exception {
        OrderEntity order = getOne(new QueryWrapper<OrderEntity>().lambda().eq(OrderEntity::getOrderNo, orderNo));
        if (order == null) {
            return ApiResponseUtil.getApiResponseParamsError("orderNo不存在");
        }

        String refundNo = SeqNoUtil.generateRefundSeqNo();
        WeiXinUtil.AppRefundResult appRefundResult = weiXinUtil.jsApiService.refundOrder(orderNo, refundNo, order.getPayPrice());
        if (appRefundResult.isSuccess()) {
            order.setStatus(3);
            order.setRefunded(true);
            order.setRefundNo(refundNo);
            order.setRefundTime(new Date());
            updateById(order);
        } else {
            return ApiResponseUtil.getApiResponseParamsError(JSON.toJSONString(appRefundResult));
        }

        return ApiResponseUtil.getApiResponseSuccess();
    }

    /**
     * 为订单下发物料
     */
    private boolean assignForOrder(OrderEntity order) throws Exception {
        // 1. 检查goodsId是否正确
        GoodsEntity goods = goodsService.getById(order.getGoodsId());
        if (goods == null) {
            return false;
        }

        if (goods.getType() == 1) { // 会员
            // 更新会员有效期
            userVipService.addUserVip(String.valueOf(order.getUserId()), goods.getGoodsTag(), UserScoreUpdateEnum.BUY_VIP);

        } else if (goods.getType() == 2) { // 积分
            int score = goods.getAssignCount();
            userScoreService.update(score, order.getUserId(), UserScoreUpdateEnum.BUY_SCORE);
        }

        return true;
    }
}
