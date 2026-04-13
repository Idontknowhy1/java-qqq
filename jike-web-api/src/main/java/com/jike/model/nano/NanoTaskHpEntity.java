package com.jike.model.nano;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("nano_tasks_hp") 
public class NanoTaskHpEntity implements Serializable {
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
    @TableId(type = IdType.AUTO) 
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
    private int status = 0;

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
    private long updateTime = System.currentTimeMillis() / 1000;

}
