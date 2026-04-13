package com.jike.model.nano;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SoroTaskDTO implements Serializable {

    @NotBlank
    private String model;

    @NotBlank
    private String prompt;
    private String referImages;

    private int duration;
    private int scale;

    private String args;
    private int count = 1;

}
