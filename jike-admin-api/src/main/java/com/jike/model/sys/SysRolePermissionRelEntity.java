package com.jike.model.sys;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_rel_role_permission") 
public class SysRolePermissionRelEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
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
