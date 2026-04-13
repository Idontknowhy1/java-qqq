package com.jike.model.order;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GoodsDTO implements Serializable {
    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private boolean disabled;

    /**
     * VIP_LV01, VIP_LV02, VIP_LV03, SCORE_60, SCORE_300, SCORE_600, SCORE_1450
     */
    private String goodsTag;

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
    private float price;

    /**
     * 
     */
    private boolean showLimitTag;

    /**
     * 1-会员，2-积分
     */
    private int type;

}
