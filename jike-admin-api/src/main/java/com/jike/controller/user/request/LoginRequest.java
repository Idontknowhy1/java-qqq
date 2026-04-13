package com.jike.controller.user.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    private String account;
    @NotBlank
    private String password;
}
