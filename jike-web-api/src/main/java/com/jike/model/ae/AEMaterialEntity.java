package com.jike.model.ae;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_ae_plugin_materials") 
public class AEMaterialEntity implements Serializable {
    /**
     * 
     */
    private String coverUrl;

    /**
     * 
     */
    private boolean enabled;

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
    private String authorRight;

    /**
     * 
     */
    private int typeId;

    /**
     * 
     */
    private String url;

    private String module;

}
