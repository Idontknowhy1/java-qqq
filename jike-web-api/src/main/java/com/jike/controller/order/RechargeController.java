package com.jike.controller.order;

import com.jike.controller.order.request.CreateRechargeOrderRequest;
import com.jike.service.order.RechargeOrderService;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 积分充值订单 Controller
 */
@RequestMapping("/recharge/v1")
@RestController
@Slf4j
public class RechargeController {

    @Autowired
    private RechargeOrderService rechargeOrderService;

    /**
     * 创建充值订单
     * POST /api/recharge/native/create
     */
    @PostMapping("/native/create")
    public ApiResponse createOrder(@RequestBody CreateRechargeOrderRequest request) {
        try {
            return rechargeOrderService.createOrder(request);
        } catch (Exception e) {
            log.error("创建充值订单失败", e);
            return ApiResponseUtil.getApiResponseFailure("创建订单失败");
        }
    }

    /**
     * 查询订单状态
     * GET /api/recharge/native/status?orderId=xxx
     */
    @GetMapping("/native/status")
    public ApiResponse getOrderStatus(@RequestParam String orderId) {
        try {
            return rechargeOrderService.getOrderStatus(orderId);
        } catch (Exception e) {
            log.error("查询订单状态失败", e);
            return ApiResponseUtil.getApiResponseFailure("查询订单状态失败");
        }
    }
}