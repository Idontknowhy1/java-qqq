package com.jike.model.bus;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MaterialSquareVO implements Serializable {
    /**
     * 
     */
    private long assId;

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
    private String imageUrl;

    /**
     * 
     */
    private String model;

    /**
     * 
     */
    private String name;

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
    private String size;

    /**
     * 
     */
    private int sortIndex;

}
