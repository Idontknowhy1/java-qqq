package com.jike.controller.nano.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ScriptCommitRequest {
    @NotBlank
    String referImages;

    @NotBlank
    String uuid;

    @NotBlank
    @Pattern(regexp = "^(ms|hp)$")
    String model;
}
