package com.jike.model.sys;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_permissions") 
public class SysPermissionEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * MENU-菜单，FUNC-功能点
     */
    private String type;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private int pid;

    /**
     * 
     */
    private String code;

    /**
     * 
     */
    private String route;

    /**
     * 
     */
    private int orderIndex;

    private boolean enable;

}
