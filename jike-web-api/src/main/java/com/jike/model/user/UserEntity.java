package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("users") 
public class UserEntity implements Serializable {
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
    private long updateTime = System.currentTimeMillis() / 1000;

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

    private boolean initedVip;

}
