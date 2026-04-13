package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("user_login") 
public class UserLoginEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private long deleteTime;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private long id;

    /**
     * 用户名或者openid
     */
    private String info;

    /**
     * 1用户名 2手机号 3微信
     */
    private String type;

    /**
     * 
     */
    private long updateTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    private long userId;

}
