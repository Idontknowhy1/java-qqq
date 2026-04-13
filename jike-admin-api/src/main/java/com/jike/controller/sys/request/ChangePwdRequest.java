package com.jike.controller.sys.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePwdRequest {
    @NotBlank
    private String oldPwd;
    @NotBlank
    private String newPwd;
}
