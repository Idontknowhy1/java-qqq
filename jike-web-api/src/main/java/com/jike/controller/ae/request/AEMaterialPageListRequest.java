package com.jike.controller.ae.request;

import com.jjs.common.BasePageQuery;
import lombok.Data;

import java.io.Serializable;

@Data
public class AEMaterialPageListRequest extends BasePageQuery {
    private int typeId;
}
