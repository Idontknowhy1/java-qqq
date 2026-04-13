package com.jike.controller.inner.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InnerAddUserVipRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String  vipTag;
}
