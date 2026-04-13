package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserVO implements Serializable {

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
    private long createTime;

    /**
     * 
     */
    private String id;

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
    private String roles;

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

    private boolean pluginMember;
    private boolean materialMember;
//    private boolean aiGenMember;
//    private boolean imageSplitMember;

//    private String aiGenMemberExpireAt;
//    private String imageSplitMemberExpireAt;
    private String pluginMemberExpireAt;
    private String materialMemberExpireAt;

    private String vipExpireAt;

}
