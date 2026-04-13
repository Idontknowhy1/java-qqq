package com.jike.controller.nano.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TaskHpDTO {
    @NotBlank
    private String taskId;
    @NotBlank
    private String args;
}
