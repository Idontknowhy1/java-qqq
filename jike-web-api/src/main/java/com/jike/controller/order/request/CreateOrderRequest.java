package com.jike.controller.order.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrderRequest {
    @NotNull
    private int goodsId;
}
