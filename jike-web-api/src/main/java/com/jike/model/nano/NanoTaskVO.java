package com.jike.model.nano;

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
    private long createTime;
    private String createTimeText;

    /**
     * 
     */
    private long id;
    private String idText;

    /**
     * 
     */
    private String latestFailReason;

    /**
     * 
     */
    private boolean isHp;
    private boolean isMs;
    private String  model;

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
    private String size;

    /**
     * 
     */
    private int status;

    /**
     * 
     */
    private long userId;

    /**
     * 比例，16:9、3:4 这种格式
     */
    private String aspectRatio;
    private String args;

    private String outputs;

}
