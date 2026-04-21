package com.jike.controller.order.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建充值订单请求
 */
@Data
public class CreateRechargeOrderRequest {

    /**
     * 用户ID
     */
    @NotBlank(message = "userId不能为空")
    private String userId;

    /**
     * 套餐ID：pkg_500, pkg_2000, pkg_5000, pkg_12000
     */
    @NotBlank(message = "packageId不能为空")
    private String packageId;
}