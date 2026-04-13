package com.jike.model.base;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("banners") 
public class BannerEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private long id;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String image;

    /**
     * 
     */
    private String link;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String sort;

    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private long updateTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private long deleteTime;

}
