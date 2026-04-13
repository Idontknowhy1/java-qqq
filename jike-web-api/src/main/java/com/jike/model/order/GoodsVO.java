package com.jike.model.order;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GoodsVO implements Serializable {
    /**
     * 
     */
    private String description;

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
    private float price;

    /**
     * 1-会员，2-积分
     */
    private int type;
    private String goodsTag;

    private boolean showLimitTag;

}
