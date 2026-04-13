package com.jike.model.ae;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AEMaterialTypeVO implements Serializable {
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
    private int orderIndex;

    /**
     * 
     */
    private int pId;

    /**
     * 
     */
    private int type;

}
