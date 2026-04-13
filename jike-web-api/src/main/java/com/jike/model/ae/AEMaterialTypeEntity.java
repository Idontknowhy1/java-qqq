package com.jike.model.ae;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_ae_plugin_material_types") 
public class AEMaterialTypeEntity implements Serializable {
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
    private int pId;

    /**
     * 
     */
    private String module;

}
