package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ChatMessageDTO implements Serializable {
    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private int convId;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private long userId;

}
