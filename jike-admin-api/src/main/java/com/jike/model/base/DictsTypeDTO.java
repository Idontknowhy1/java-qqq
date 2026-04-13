package com.jike.model.base;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DictsTypeDTO implements Serializable {
    /**
     * 
     */
    private String code;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String name;

}
