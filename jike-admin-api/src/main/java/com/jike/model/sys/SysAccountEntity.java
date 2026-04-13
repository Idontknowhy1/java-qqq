package com.jike.model.sys;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_accounts") 
public class SysAccountEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private String account;
    private String name;

    /**
     * 
     */
    private String password;

    /**
     * 
     */
    private int roleId;

    /**
     * 
     */
    private boolean locked;

    /**
     * 
     */
    private String memo;

}
