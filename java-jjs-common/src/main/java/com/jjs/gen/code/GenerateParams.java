package com.jjs.gen.code;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateParams {
    private String tableName;
    private String clazzName;
    private String packageName;
    /**
     * 生成代码后是否自动部署到项目中
     */
    private Boolean autoDeploy = false;

    private String basePackage;

    private String apiVersion = "";
}
