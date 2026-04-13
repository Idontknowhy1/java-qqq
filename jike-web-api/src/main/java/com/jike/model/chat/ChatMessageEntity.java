package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("tb_script_genre_messages") 
public class ChatMessageEntity implements Serializable {
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
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private long userId;

    // 方向1-发送，2-接收
    private int direction = 1;

}
