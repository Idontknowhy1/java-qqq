package com.jike.model.materials;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_material_square") 
public class MaterialSquareEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    private long assId;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private long id;

    /**
     * 
     */
    private String imageUrl;

    /**
     * 
     */
    private String model;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private String prompt;

    /**
     * 
     */
    private String referImages;

    /**
     * 
     */
    private String size;

    /**
     * 
     */
    private int sortIndex;

}
