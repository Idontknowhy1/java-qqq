package com.jike.model.nano;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class NanoTaskDTO implements Serializable {

    private long id;

    private String prompt;
    @NotEmpty
    private String modelType;

    @NotEmpty
    private List<String> referImages;

    /**
     * 比例，16:9、3:4 这种格式
     */
    private String aspectRatio;

    // 2k， 4k
    private String resolution = "2k";

}
