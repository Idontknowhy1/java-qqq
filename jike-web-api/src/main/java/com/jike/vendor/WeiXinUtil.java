package com.jike.vendor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jike.constant.AppConst;
import com.jike.redis.LockUtil;
import com.jike.redis.RedisCacheUtil;
import com.jike.utils.HttpTool;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WeiXinUtil {

    private String createConfigError;
    private Config config;

    @Autowired
    private WeiXinConfig weixinConfig;

    public JSApiService jsApiService = new JSApiService();
    public OfficeAccount officeAccount = new OfficeAccount();

    @PostConstruct
    public void createConfig(){
        try {

            jsApiService.weixinConfig = weixinConfig;

            // 使用自动更新平台证书的RSA配置
            // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
            File file = new File(weixinConfig.getPrivateKeyPath());
            if (file.exists()) {
                config = new RSAAutoCertificateConfig.Builder()
                        .merchantId(weixinConfig.getMchId())
                        .privateKeyFromPath(weixinConfig.getPrivateKeyPath())
                        .merchantSerialNumber(weixinConfig.getMchSerialNo())
                        .apiV3Key(weixinConfig.getApiV3Key())
                        .build();
            } else {
                createConfigError = weixinConfig.getPrivateKeyPath() + " 文件不存在";
                log.error("微信 config 初始化失败， " + createConfigError, new Exception(createConfigError));
            }
        } catch (Exception ex) {
            createConfigError = ex.getLocalizedMessage();
            log.error("error = ex.getLocalizedMessage()",ex);
        }
    }

    /**
     * App Service
     */
    public class JSApiService {

         WeiXinConfig weixinConfig;

        /**
         * 调用微信下单接口
         */
        public CreateOrderResult createOrder(String goodsName, int price, String orderNo, String openId) {

            CreateOrderResult result = new CreateOrderResult();
            if (config == null) {
                result.setSuccess(false);
                result.setError(createConfigError);
                return result;
            }

            try {

                com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension
                        service = new com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension.Builder().config(config).build();
                // 构建service
                com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest
                        request = new com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest();
                com.wechat.pay.java.service.payments.jsapi.model.Amount amount = new com.wechat.pay.java.service.payments.jsapi.model.Amount();
                amount.setTotal(price);
                request.setAmount(amount);
                request.setAppid(weixinConfig.getOfficeAppId());
                request.setMchid(weixinConfig.getMchId());
                request.setDescription(goodsName);
                request.setNotifyUrl(weixinConfig.getNotifyUrl());
                request.setOutTradeNo(orderNo);

                com.wechat.pay.java.service.payments.jsapi.model.Payer payer = new com.wechat.pay.java.service.payments.jsapi.model.Payer();
                payer.setOpenid(openId);
                request.setPayer(payer);

                // 调用下单方法，得到应答
                com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);

                String json = JSON.toJSONString(response);
                System.out.println(json);

                // 签名文档
//                https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_4.shtml

                result.setSuccess(true);
                result.setSign(JSON.toJSONString(response));

            } catch (Exception ex) {
                log.error("error 创建微信订单 openId = " + openId + " " + ex.getLocalizedMessage(),ex);
                result.setSuccess(false);
                result.setError(ex.getLocalizedMessage());
            }

            return result;

        }

        /**
         * 检查订单支付状态
         */
        public CheckOrderResult checkOrder(String orderNo) {
            // 向微信发起验证
            com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest
                    queryRequest = new com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest();
            queryRequest.setMchid(weixinConfig.getMchId());
            queryRequest.setOutTradeNo(orderNo);

            CheckOrderResult  checkOrderResult = new CheckOrderResult();

            try {
                // 构建service
                com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension
                        service = new com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension.Builder().config(config).build();
                Transaction result = service.queryOrderByOutTradeNo(queryRequest);
                String tradeState = result.getTradeState().name(); // "SUCCESS"
                String outTradeNo = result.getOutTradeNo();
                if (orderNo.equals(outTradeNo) && tradeState.equals("SUCCESS")) {
                    checkOrderResult.setPayed(true);
                    checkOrderResult.setTranscationId(result.getTransactionId());
                    checkOrderResult.setPrice(result.getAmount().getPayerTotal() / 100.0);
                }
            } catch (ServiceException e) {
                log.error("error = "+e.getLocalizedMessage(), e);
                checkOrderResult.setError(e.getLocalizedMessage());
            }

            return checkOrderResult;
        }

        /**
         * 退款
         */
        public AppRefundResult refundOrder(String orderNo, String refundNo, float totalPrice) {
//            com.wechat.pay.java.service.payments.nativepay.model
            AppRefundResult refundResult = new AppRefundResult();
            try {
                RefundService service = new RefundService.Builder().config(config).build();
                CreateRequest request = new CreateRequest();
                request.setOutTradeNo(orderNo);
                request.setOutRefundNo(refundNo);

                AmountReq amountReq = new AmountReq();
                amountReq.setRefund((long) (totalPrice * 100));
                amountReq.setTotal((long) (totalPrice * 100));
                amountReq.setCurrency("CNY");
                request.setAmount(amountReq);

                Refund refund = service.create(request);
                refundResult.setSuccess(true);
                refundResult.setExtra(JSON.toJSONString(refund));

            } catch (Exception ex) {
                refundResult.setSuccess(false);
                refundResult.setExtra(ex.toString());
            }
            return refundResult;
        }

        /**
         * 公众号 code兑换openId
         */
        public WxCode2SessionResponse officeCode2Session(String code) {
            HashMap hashMap = new HashMap();
            hashMap.put("appid", weixinConfig.getOfficeAppId());
            hashMap.put("secret", weixinConfig.getOfficeSecret());
            hashMap.put("grant_type", "authorization_code");
            hashMap.put("code", code);

            try {
                String bodyString = HttpTool.get("https://api.weixin.qq.com/sns/oauth2/access_token",hashMap);
                WxCode2SessionResponse response = JSON.parseObject(bodyString, WxCode2SessionResponse.class);
                if (response.getErrcode() == 0) {
                    response.setSuccess(true);
                }
                log.info("公众号code兑换结果：{}", JSON.toJSONString(response));
                return response;
            } catch (Exception e) {
                log.error("---小程序code兑换OpenId失败",e);
            }
            return null;
        }
    }

    public class OfficeAccount {
        /**
         * 更新公众号菜单
         * @param map
         * @return
         */
        public String updateMenu(HashMap map) {
            try {
                AccessToken accessToken = getAccessToken();
                if (accessToken == null || accessToken.getAccess_token().isEmpty()) {
                    return null;
                }
                String bodyString = HttpTool.post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken.getAccess_token(),map);
                log.info("更新公众号菜单 结果：{}", bodyString);
                return bodyString;
            } catch (Exception e) {
                log.error("---更新公众号菜单 失败",e);
            }
            return null;
        }

        public String getMenu() {
            try {
                AccessToken accessToken = getAccessToken();
                if (accessToken == null || accessToken.getAccess_token().isEmpty()) {
                    return null;
                }
                log.info("获取公众号菜单 token：" + accessToken.getAccess_token());
                String bodyString = HttpTool.get("https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=" + accessToken.getAccess_token());
                log.info("获取公众号菜单 结果：{}", bodyString);
                return bodyString;
            } catch (Exception e) {
                log.error("---获取公众号菜单 失败",e);
            }
            return null;
        }

        /**
         * 获取 access_token
         */
        public AccessToken getAccessToken() {
            return getAccessToken(AppConst.WX_OFFICE_ACCOUNT_APP_ID, AppConst.WX_OFFICE_ACCOUNT_SECRET);
        }
        public AccessToken getAccessToken(String appid, String secret) {

            String redisKey = "accesstoken:" + appid;
//            RLock lock = LockUtil.getLock(redisKey);
            try {
//                if (lock.tryLock(30, TimeUnit.SECONDS) == false) {
//                    log.error("获取 getAccessToken 锁失败 appid = " + appid, new Exception("获取 getAccessToken 锁失败 appid = " + appid));
//                    return null;
//                }
//
//                // 从缓存获取
//                String redisValue = RedisCacheUtil.getString(redisKey);
//                if (redisValue != null) {
//                    AccessToken result = new AccessToken();
//                    result.setAccess_token(redisValue);
//                    return result;
//                }

                String url = "https://api.weixin.qq.com/cgi-bin/token";
                HashMap params = new HashMap();
                params.put("grant_type", "client_credential");
                params.put("appid", appid);
                params.put("secret", secret);
                String requestResult = HttpTool.get(url, params);
                if (requestResult == null || requestResult.isEmpty()) {
                    return null;
                }
                log.info("获取公众号getAccessToken结果 " + requestResult);
                AccessToken result = JSONObject.parseObject(requestResult, AccessToken.class);

                // 提交到Redis缓存, 有效期2小时
                RedisCacheUtil.setString(redisKey, result.getAccess_token(), 7200 - 10);

                return result;

            } catch (Exception ex) {
                log.error("getAccessToken 失败", ex);
            } finally {
                // 确保无论如何都释放锁
//                if (lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
            }
            return null;
        }
    }

    /**
     * 小程序code兑换OpenId
     */
    @Data
    public static class WxCode2SessionResponse {
        private String openid;
        private String unionid;
        private String session_key;
        private int errcode;
        private String errmsg;
        private boolean success;
    }

    @Getter
    @Setter
    public class CreateOrderResult {
        private Boolean success;
        private String sign;
        private String error;
    }

    @Getter
    @Setter
    public class CheckOrderResult {
        private boolean payed;
        private String transcationId;
        private String error;
        private double price;
        private String tradeState;
    }

    @Getter
    @Setter
    public class AppRefundResult {
        private boolean success;
        private String extra;
    }

    /**
     * Native扫码支付订单结果
     */
    @Getter
    @Setter
    public static class NativeOrderResult {
        private boolean success;
        private String codeUrl;
        private String error;
    }

    /**
     * 调用微信Native下单接口（扫码支付）
     */
    public NativeOrderResult createNativeOrder(String description, int amountFen, String outTradeNo, String attach) {
        NativeOrderResult result = new NativeOrderResult();
        if (config == null) {
            result.setSuccess(false);
            result.setError(createConfigError);
            return result;
        }

        try {
            // 使用 Native 服务
            com.wechat.pay.java.service.payments.nativepay.NativePayService service =
                    new com.wechat.pay.java.service.payments.nativepay.NativePayService.Builder().config(config).build();

            com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request =
                    new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();

            // 设置金额（分）
            com.wechat.pay.java.service.payments.nativepay.model.Amount amount =
                    new com.wechat.pay.java.service.payments.nativepay.model.Amount();
            amount.setTotal(amountFen);
            amount.setCurrency("CNY");
            request.setAmount(amount);

            request.setAppid(weixinConfig.getOfficeAppId());
            request.setMchid(weixinConfig.getMchId());
            request.setDescription(description);
            request.setNotifyUrl(weixinConfig.getNotifyUrl());
            request.setOutTradeNo(outTradeNo);
            request.setAttach(attach);

            // 调用下单方法
            com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse response = service.prepay(request);

            result.setSuccess(true);
            result.setCodeUrl(response.getCodeUrl());

        } catch (ServiceException e) {
            log.error("创建微信Native订单失败: {}", e.getLocalizedMessage(), e);
            result.setSuccess(false);
            result.setError(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error("创建微信Native订单异常: {}", e.getLocalizedMessage(), e);
            result.setSuccess(false);
            result.setError(e.getLocalizedMessage());
        }

        return result;
    }

    /**
     * 检查订单支付状态
     */
    public CheckOrderResult checkNativeOrder(String outTradeNo) {
        CheckOrderResult checkOrderResult = new CheckOrderResult();

        try {
            com.wechat.pay.java.service.payments.nativepay.NativePayService service =
                    new com.wechat.pay.java.service.payments.nativepay.NativePayService.Builder().config(config).build();

            com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest queryRequest =
                    new com.wechat.pay.java.service.payments.nativepay.model.QueryOrderByOutTradeNoRequest();
            queryRequest.setMchid(weixinConfig.getMchId());
            queryRequest.setOutTradeNo(outTradeNo);

            Transaction result = service.queryOrderByOutTradeNo(queryRequest);
            String tradeState = result.getTradeState().name();

            if (outTradeNo.equals(result.getOutTradeNo()) && "SUCCESS".equals(tradeState)) {
                checkOrderResult.setPayed(true);
                checkOrderResult.setTranscationId(result.getTransactionId());
                checkOrderResult.setPrice(result.getAmount().getPayerTotal() / 100.0);
            }
            checkOrderResult.setTradeState(tradeState);

        } catch (ServiceException e) {
            log.error("查询微信订单失败: {}", e.getLocalizedMessage(), e);
            checkOrderResult.setError(e.getLocalizedMessage());
        }

        return checkOrderResult;
    }

    @Data
    public static class AccessToken {
//        https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html

        /**
         * 获取到的凭证
         */
        String access_token;
        /**
         * 凭证有效时间，单位：秒
         */
        Integer expires_in;
    }

}
