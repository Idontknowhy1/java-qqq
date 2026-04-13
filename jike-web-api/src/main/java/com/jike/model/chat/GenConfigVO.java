package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class GenConfigVO implements Serializable {
    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private String icon;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private int orderIndex;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String type;

}
