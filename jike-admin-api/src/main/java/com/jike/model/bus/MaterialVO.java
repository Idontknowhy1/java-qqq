package com.jike.model.bus;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MaterialVO implements Serializable {

    /**
     * 
     */
    private String baiduUrl;

    /**
     * 
     */
    private long categoryId;
    private String categoryName;

    /**
     * 
     */
    private String coverImage;

    /**
     * 
     */
    private String createTime;

    /**
     * 
     */
    private String details;

    /**
     * 
     */
    private String id;

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
    private int viewNum;

}
