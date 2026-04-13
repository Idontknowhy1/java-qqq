package com.jike.model.bus;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MaterialDTO implements Serializable {

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
    private String details;

    /**
     * 
     */
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
    private int viewNum;

}
