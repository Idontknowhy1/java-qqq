package com.jike.model.sys;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SysAccountVO implements Serializable {
    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String account;

    private String name;

}
