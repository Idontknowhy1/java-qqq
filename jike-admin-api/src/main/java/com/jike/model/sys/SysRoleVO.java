package com.jike.model.sys;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SysRoleVO implements Serializable {
    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private boolean disabled;

}
