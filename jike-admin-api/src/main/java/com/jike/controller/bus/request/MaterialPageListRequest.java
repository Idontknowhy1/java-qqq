package com.jike.controller.bus.request;

import com.jjs.common.BasePageQuery;
import lombok.Data;

@Data
public class MaterialPageListRequest extends BasePageQuery {
    private int categoryId;
}
