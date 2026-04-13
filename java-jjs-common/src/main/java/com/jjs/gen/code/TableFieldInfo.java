package com.jjs.gen.code;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TableFieldInfo implements Serializable {
    private String columnName;
    private String dataType;
    private String columnComment;
    private String columnType;
}
