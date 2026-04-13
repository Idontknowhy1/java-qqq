package com.jike.model.base;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BannerDTO implements Serializable {
    /**
     * 
     */
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
    private long deleteTime;

}
