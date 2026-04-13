package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class GenConfigDTO implements Serializable {
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
    private String instruction;

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
