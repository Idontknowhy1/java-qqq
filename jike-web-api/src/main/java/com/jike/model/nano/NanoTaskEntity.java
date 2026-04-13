package com.jike.model.nano;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("nano_tasks") 
public class NanoTaskEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

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
    @TableId(type = IdType.AUTO) 
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
    private int retryNum;

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
    private String thirdTaskId;

    /**
     * 
     */
    private long updateTime = System.currentTimeMillis() / 1000;

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
