package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserVipVO implements Serializable {
    /**
     * 
     */
    private long createTime;

    /**
     * 
     */
    private long expireTime;

    /**
     * 
     */
    private boolean expired;

    /**
     * 
     */
    private String expiredReason;

    /**
     * 
     */
    private int id;

    /**
     * 最后一次分配积分时间
     */
    private long lastAssignScoreTime;

    /**
     * 
     */
    private String memo;

    /**
     * 
     */
    private float price;

    /**
     * 
     */
    private long userId;

    /**
     * 
     */
    private String vip;

}
