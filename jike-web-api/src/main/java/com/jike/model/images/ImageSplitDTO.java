package com.jike.model.images;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ImageSplitDTO implements Serializable {
    /**
     * 
     */
    private String detail;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String imageUrl;

    /**
     * 
     */
    private String title;

}
