package com.jike.controller.inner.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class InnerAddUserScoreRequest {
    @NotBlank
    private String userId;

    private long  score;
}
