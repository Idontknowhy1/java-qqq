package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ConversationsVO implements Serializable {
    /**
     * 
     */
    private String createTime;

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
    private String title;

    /**
     * 
     */
    private String updateTime;

    /**
     * 
     */
    private long userId;

}
