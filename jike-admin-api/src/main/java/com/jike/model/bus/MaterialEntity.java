package com.jike.model.bus;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("materials") 
public class MaterialEntity implements Serializable {

    /**
     * 
     */
    private String baiduUrl;

    /**
     * 
     */
    private long categoryId;

    /**
     * 
     */
    private String coverImage;

    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private String details;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private long id;

    /**
     * 
     */
    private String intro;

    /**
     * 
     */
    private String properties;

    /**
     * 
     */
    private int sort;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private long updateTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private long userId;

    /**
     * 
     */
    private int viewNum;

}
