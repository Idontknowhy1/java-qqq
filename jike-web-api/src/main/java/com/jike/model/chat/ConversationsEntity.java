package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("tb_script_genre_convs") 
public class ConversationsEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private String icon;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private String instruction;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private long updateTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private long userId;

}
