package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserDTO implements Serializable {
    /**
     * ai辅助到期时间
     */
    private long aiGenMemberExpireAt;

    /**
     * 
     */
    private String avatar;

    /**
     * 
     */
    private long baiduUk;

    /**
     * 
     */
    private long deleteTime;

    /**
     * 
     */
    private long id;

    /**
     * 图片拆分到期时间
     */
    private long imageSplitMemberExpireAt;

    /**
     * 
     */
    private long materialMemberExpireAt;

    /**
     * 
     */
    private String mobile;

    /**
     * 
     */
    private String nickname;

    /**
     * 
     */
    private String password;

    /**
     * 
     */
    private long pluginMemberExpireAt;

    /**
     * 
     */
    private String roles;

    /**
     * 
     */
    private String salt;

    /**
     * 
     */
    private String username;

    /**
     * 
     */
    private String uuid;

    /**
     * Vip级别，默认0
     */
    private int vipLevel;

}
