package com.jike.controller.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jike.common.model.score.UserScoreUpdateEnum;
import com.jike.controller.inner.request.InnerAddUserScoreRequest;
import com.jike.controller.inner.request.InnerAddUserVipRequest;
import com.jike.model.score.UserScoreUpdateResult;
import com.jike.model.user.UserEntity;
import com.jike.model.user.UserVO;
import com.jike.request.AppRequestHeader;
import com.jike.service.order.OrderService;
import com.jike.service.score.UserScoreService;
import com.jike.service.user.UserService;
import com.jike.service.user.UserVipService;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseEnum;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequestMapping("/inner")
@RestController
@Slf4j
public class InnerController {

    @Autowired
    UserService userService;
    @Autowired
    UserVipService userVipService;
    @Autowired
    UserScoreService userScoreService;
    @Autowired
    OrderService orderService;

    @PostMapping("/addUserVip")
    @ResponseBody
    public ApiResponse updateUserVip(@Validated @RequestBody InnerAddUserVipRequest request) {
        try {
            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().lambda()
                    .eq(UserEntity::getId, request.getUserId()));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("userId无效");
            }

            return userVipService.addUserVip(String.valueOf(userEntity.getId()), request.getVipTag(), UserScoreUpdateEnum.ADMIN_ADD_VIP);
        } catch (Exception e) {
            log.error("Inner 添加会员失败",e);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 管理员添加积分
     */
    @PostMapping("/addUserScore")
    public ApiResponse addScore(@Validated @RequestBody InnerAddUserScoreRequest request) throws Exception{
        try {

            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().lambda()
                    .eq(UserEntity::getId, request.getUserId()));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("userId无效");
            }

            UserScoreUpdateResult updateResult = userScoreService.update(request.getScore(), userEntity.getId(), UserScoreUpdateEnum.ADMIN_ADD_SCORE);
            return ApiResponseUtil.getApiResponseSuccess(updateResult.getRecord());

        } catch (Exception e){
            log.error("Inner 添加积分 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

    /**
     * 获取用户会员信息
     */
    @GetMapping("/user/info")
    public ApiResponse getUserInfo(@RequestParam String uuid) throws Exception{
        try {
            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().lambda()
                    .eq(UserEntity::getUuid, uuid));
            if (userEntity == null) {
                return ApiResponseUtil.getApiResponseParamsError("uuid无效");
            }

            UserVO userVo = userService.getUserById(String.valueOf(userEntity.getId()));

            return ApiResponseUtil.getApiResponseSuccess(userVo);

        } catch (Exception e){
            log.error("Inner 添加积分 失败，error = " + e.toString(), e);
        }
        return ApiResponseUtil.getApiResponseFailure();
    }

//    {
//        "code": 10001,
//            "msg": "{\"extra\":\"com.wechat.pay.java.core.exception.ServiceException: Wrong HttpStatusCode[403]\\nhttpResponseBody[{\\\"code\\\":\\\"NOT_ENOUGH\\\",\\\"message\\\":\\\"基本账户余额不足，请充值后重新发起\\\"}]\\tHttpRequest[{\\\"http_method\\\":\\\"POST\\\",\\\"url\\\":\\\"https://api.mch.weixin.qq.com/v3/refund/domestic/refunds\\\",\\\"uri\\\":\\\"https://api.mch.weixin.qq.com/v3/refund/domestic/refunds\\\",\\\"headers\\\":{\\\"headers\\\":{\\\"Authorization\\\":\\\"WECHATPAY2-SHA256-RSA2048 mchid=\\\\\\\"1676428333\\\\\\\",nonce_str=\\\\\\\"Baik3LolLpg9OxqrNXpBB8LcU9nXIoma\\\\\\\",timestamp=\\\\\\\"1760396670\\\\\\\",serial_no=\\\\\\\"6A6BC3FA8D75613F27DFC42FE459B5A8932A9304\\\\\\\",signature=\\\\\\\"EjpH6kDizKjMrnCN1F91KTX6EE8y4AprmQ236L/j2BeFVjCREj8eOCo/j4WXJZ7K6hXS0FAc5JN2XX3EtwbOv7ailzqcOQbcwTi7OmYUmEyrUkkcTw3AHGcnq9CR83P9G4IZrnD4PzHsJLE+y5G25zAYQjabkt9tP9yl7VaOO0MymIudaZRAvdDx69kark4TJ0RqOT96PV3UdnoY3whJo68YazokFlKAXUDY6ObYCAxDm85S5lRW75Jyq5ekdz5uR1DQyKFsW6iUrvEkuGfAK/Lo/vlws928uPDzMmNYIWiq4fDBThCaWgYX06MjJ4Hf4NbskkQcfaFaXsjTcTduQg==\\\\\\\"\\\",\\\"Accept\\\":\\\"application/json\\\",\\\"User-Agent\\\":\\\"WechatPay-Java/0.2.17 (Mac OS X/15.6.1) Java/17.0.14 Credential/WechatPay2Credential Validator/WechatPay2Validator okhttp3/null\\\",\\\"Content-Type\\\":\\\"application/json\\\",\\\"Wechatpay-Serial\\\":\\\"68A948FAB305EC0227580AFE8B97690F58E7FE16\\\"}},\\\"body\\\":{\\\"body\\\":\\\"{\\\\\\\"out_trade_no\\\\\\\":\\\\\\\"1427036505984991232\\\\\\\",\\\\\\\"out_refund_no\\\\\\\":\\\\\\\"REF1427552632917983232\\\\\\\",\\\\\\\"amount\\\\\\\":{\\\\\\\"refund\\\\\\\":30000,\\\\\\\"total\\\\\\\":30000,\\\\\\\"currency\\\\\\\":\\\\\\\"CNY\\\\\\\"}}\\\"}}]\",\"success\":false}",
//            "data": null
//    }

    /**
     * 订单退款
     */
    @GetMapping("/order/refund")
    @ResponseBody
    public ApiResponse orderRefund(@RequestParam String orderNo) {
        try {
            return orderService.refundOrder(orderNo);
        } catch (Exception ex) {
            log.error("订单退款失败", ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 订单校验
     */
    @GetMapping("/order/checkstate")
    @ResponseBody
    public ApiResponse checkOrder(@RequestParam String orderNo) {
        try {
            ApiResponse response = orderService.checkOrder(orderNo);
            if (response.getCode() == 10001) {
                return ApiResponseUtil.getApiResponseSuccess(new HashMap<>(){{
                    put("payed", false);
                    put("msg", response.getMsg());
                }});
            } else if (response.getCode() == 10000) {
                return ApiResponseUtil.getApiResponseSuccess(new HashMap<>(){{
                    put("payed", true);
                    put("msg", "检测到该订单已支付，状态已切换为“已支付”");
                }});
            }
            return response;
        } catch (Exception ex) {
            log.error("订单校验失败", ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }
}
