package com.jike.controller.order.request;

import com.jjs.common.BasePageQuery;
import lombok.Data;

@Data
public class SearchOrderPageListRequest extends BasePageQuery {
    private String userUUID = "";
    private int goodsId = 0;
    private int goodType = 1;
    private String orderNo = "";
    private int status = 2;
    private long beginTime = 0;
    private long endTime = 0;
}
