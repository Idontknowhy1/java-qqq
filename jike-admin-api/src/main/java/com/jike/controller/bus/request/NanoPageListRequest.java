package com.jike.controller.bus.request;

import com.jjs.common.BasePageQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NanoPageListRequest extends BasePageQuery {
    private String uuid;
}