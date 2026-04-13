package com.jike.model.ae;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AEMaterialVO implements Serializable {
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
    private int id;

    /**
     * 
     */
    private String authorRight;

    /**
     * 
     */
    private int orderIndex;

    /**
     * 
     */
    private String right;

    /**
     * 
     */
    private int typeId;

    /**
     * 
     */
    private String url;
    private String  name;

}
