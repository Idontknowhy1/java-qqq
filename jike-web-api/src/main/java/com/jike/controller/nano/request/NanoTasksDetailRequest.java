package com.jike.controller.nano.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class NanoTasksDetailRequest {
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<String> ids;
}
