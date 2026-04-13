package com.jike.model.sys;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SysRolePermissionRelDTO implements Serializable {
    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private int roleId;

    /**
     * 
     */
    private int perId;

}
