package com.jike.model.ae;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AEMaterialDTO implements Serializable {

    private int id;
    /**
     * 
     */
    private String coverUrl;

    /**
     * 
     */
    private String name;

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

}
