package com.jike.model.nano;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NanoTaskHpVO implements Serializable {
    /**
     * 
     */
    private long createTime;

    /**
     * 
     */
    private long deleteTime;

    /**
     * 
     */
    private long id;

    /**
     * 
     */
    private String referImage;

    /**
     * 
     */
    private String resultImage;

    /**
     * 
     */
    private int status;

    /**
     * 
     */
    private long taskId;

    /**
     * 
     */
    private String thirdTaskId;

    /**
     * 
     */
    private long updateTime;

}
