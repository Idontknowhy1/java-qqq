package com.jike.model.base;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BannerVO implements Serializable {
    /**
     * 
     */
    private String id;

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
    private long createTime;

    /**
     * 
     */
    private long updateTime;

    /**
     * 
     */
    private long deleteTime;

}
