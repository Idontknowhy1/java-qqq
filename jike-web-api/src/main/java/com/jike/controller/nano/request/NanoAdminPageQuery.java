package com.jike.controller.nano.request;

import com.jjs.common.BasePageQuery;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NanoAdminPageQuery extends BasePageQuery {
    private String uuid;
}