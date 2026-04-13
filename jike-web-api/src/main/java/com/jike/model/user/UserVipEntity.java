package com.jike.model.user;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_user_vip") 
public class UserVipEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

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
    @TableId(type = IdType.AUTO) 
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

    /**
     * 该权限只对插件生效
     */
    private boolean pluginOnly;

}
