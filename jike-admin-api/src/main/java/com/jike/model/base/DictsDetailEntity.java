package com.jike.model.base;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_dicts_detail") 
public class DictsDetailEntity implements Serializable {
    /**
     * 
     */
    private String code;

    /**
     * 
     */
    private String enable;

    /**
     * 
     */
    private String extra;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private int orderIndex;

    /**
     * 
     */
    private String typeCode;

}
