package com.jike.model.bus;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NanoTaskVO implements Serializable {
    /**
     * 
     */
    private String args;

    /**
     * 
     */
    private String aspectRatio;

    /**
     * 
     */
    private long createTime;
    private String createTimeText;

    /**
     * 
     */
    private String id;

    /**
     * 
     */
    private String model;

    /**
     * 
     */
    private String prompt;

    /**
     * 
     */
    private String referImages;

    /**
     * 
     */
    private String resultImages;

    /**
     * 
     */
    private String status;

    private String nickname;
    private String uuid;

}
