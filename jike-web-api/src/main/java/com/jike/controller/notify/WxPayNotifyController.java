package com.jike.controller.notify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.constant.AppConst;
import com.jike.service.order.RechargeOrderService;
import com.jike.service.order.OrderService;
import com.jike.vendor.WeiXinAesUtil;
import com.jike.vendor.WeiXinConfig;
import com.jjs.base.ApiBaseControllerV2;
import com.jjs.common.BasePageQuery;
import com.jjs.common.PageListResult;
import com.jike.model.notify.WxPayNotifyEntity;
import com.jike.model.notify.WxPayNotifyVO;
import com.jike.model.notify.WxPayNotifyDTO;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseEnum;
import com.jjs.response.ApiResponseUtil;
import com.jike.service.notify.WxPayNotifyService;
import com.jjs.response.security.ResponseNoSecurity;
import lombok.extern.slf4j.Slf4j;
import com.jjs.common.DefaultMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/wechat/notify")
@Slf4j
public class WxPayNotifyController extends ApiBaseControllerV2<WxPayNotifyEntity,WxPayNotifyVO, WxPayNotifyDTO> {

    final
    WxPayNotifyService service;
    public WxPayNotifyController(WxPayNotifyService service) { this.service = service; } 
    @Override
    public WxPayNotifyService getService() { return service; } 

    public long getDtoKey(WxPayNotifyDTO obj) {  return obj.getId();} 
    public long getEntityKey(WxPayNotifyEntity obj) {  return obj.getId();} 

    protected Class<WxPayNotifyVO> getVoClass() { return WxPayNotifyVO.class; }
    protected Class<WxPayNotifyDTO> getDtoClass() { return WxPayNotifyDTO.class; }
    protected Class<WxPayNotifyEntity> getEntityClass() { return WxPayNotifyEntity.class; }

    @Autowired
    WeiXinConfig weiXinConfig;
    @Autowired
    OrderService orderService;
    @Autowired
    RechargeOrderService rechargeOrderService;

    @PostMapping("/pay-result")
    @ResponseNoSecurity
    public ResponseEntity<HashMap> wxNotify(@RequestBody HashMap hashMap) {
        String body = JSON.toJSONString(hashMap);
        log.info("wxNotify 收到推送 " + body);
        try {

            // 解密
            HashMap resource = (HashMap) hashMap.get("resource");
            String resourceNonce = resource.get("nonce").toString();
            String associated_data = resource.get("associated_data").toString();
            String ciphertext = resource.get("ciphertext").toString();

            String plainText = new WeiXinAesUtil(weiXinConfig.getApiV3Key().getBytes("utf-8"))
                    .decryptToString(
                            associated_data.getBytes("utf-8"),
                            resourceNonce.getBytes("utf-8"),
                            ciphertext);
            HashMap notifyBody = JSON.parseObject(plainText, HashMap.class);

            // 解析 attach 判断订单类型
            String attach = notifyBody.get("attach") != null ? notifyBody.get("attach").toString() : "";
            String tradeState = notifyBody.get("trade_state") != null ? notifyBody.get("trade_state").toString() : "";

            // 从解密数据中获取订单号和交易ID
            if (notifyBody.get("transaction_id") == null || notifyBody.get("out_trade_no") == null) {
                log.info("---wxNotify 解密后数据不完整 \n      body = "+body);
                return failureResponse("解密后数据不完整");
            }
            String orderNo = notifyBody.get("out_trade_no").toString();
            String transactionId = notifyBody.get("transaction_id").toString();


            // 推送记录入库
            WxPayNotifyEntity notify = new WxPayNotifyEntity();
            notify.setOrderno(orderNo);
            notify.setTransid(transactionId);
            notify.setBody(plainText);
            service.save(notify);

            // 处理充值订单
            if (attach.contains("RECHARGE_POINTS")) {
                try {
                    // 打印 attach 内容，便于调试
                    log.info("处理充值订单回调, attach={}", attach);
                    JSONObject attachJson = JSON.parseObject(attach);
                    String userId = attachJson.getString("userId");
                    String packageId = attachJson.getString("packageId");
                    int points = attachJson.getIntValue("points");

                    rechargeOrderService.handlePayNotify(orderNo, tradeState, userId, packageId, points);
                    log.info("处理充值订单回调成功: orderNo={}, tradeState={}", orderNo, tradeState);
                    return successResponse();
                } catch (Exception e) {
                    log.error("处理充值订单回调失败: orderNo={}, attach={}", orderNo, attach, e);
                    return failureResponse("处理充值订单失败");
                }
            }

            // 原有的订单处理逻辑
            if (orderService.checkOrder(orderNo).getCode().equals(ApiResponseEnum.SUCCESS.getCode())) {
                return successResponse();
            } else {
                log.info("---wxNotify 本地业务处理失败 \n      body = " + body);
                return failureResponse("本地业务处理失败");
            }
//            {"mchid":"1648417222","appid":"wxeb1b1c46005501ab","out_trade_no":"1127510607277326336","transaction_id":"4200001819202307091070396477","trade_type":"APP","trade_state":"SUCCESS","trade_state_desc":"支付成功","bank_type":"OTHERS","attach":"","success_time":"2023-07-09T08:05:31+08:00","payer":{"openid":"oWSB96Gg19_egjPDgzkNowuj_BYs"},"amount":{"total":1,"payer_total":1,"currency":"CNY","payer_currency":"CNY"}}

        } catch (Exception ex) {
            log.error("---wxNotify 解密失败 \n      body = " + body + " \n      ex = " + ex,ex);
            return failureResponse("解密失败");
        }
    }

    private ResponseEntity<HashMap> failureResponse(String msg) {
        return ResponseEntity.status(500).body(new HashMap() {{
            put("code","FAIL");
            put("message",msg);
        }});
    }
    private ResponseEntity<HashMap> successResponse() {
        return ResponseEntity.status(200).body(new HashMap() {{
            put("code","success");
            put("message","success");
        }});
    }
}
