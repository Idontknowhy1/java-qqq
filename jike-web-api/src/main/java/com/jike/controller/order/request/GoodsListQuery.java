package com.jike.controller.order.request;

import lombok.Data;

@Data
public class GoodsListQuery {
    private String type;
    private boolean inner = false;
}
