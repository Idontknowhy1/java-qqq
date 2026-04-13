package com.jike.model.bus;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class NanoTaskDTO implements Serializable {
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
    private long deleteTime;

    /**
     * 
     */
    private long finishTime;

    /**
     * 
     */
    private long id;

    /**
     * 
     */
    private String latestFailReason;

    /**
     * 
     */
    private String logs;

    /**
     * 
     */
    private String model;

    /**
     * 
     */
    private String outputs;

    /**
     * 
     */
    private String prompt;

    /**
     * 
     */
    private long pushTime;

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
    private String retryNum;

    /**
     * 
     */
    private String size;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String thirdTaskId;

    /**
     * 
     */
    private long userId;

}
