package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserLoginDTO implements Serializable {
    /**
     * 
     */
    private long deleteTime;

    /**
     * 
     */
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
    private long userId;

}
