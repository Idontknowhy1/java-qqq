package com.jike.model.order;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderVO implements Serializable {
    /**
     * 
     */
    private long createTime;

    /**
     * 
     */
    private int goodsId;

    /**
     * 
     */
    private float goodsPrice;

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
    private String orderNo;

    /**
     * 
     */
    private String outTradeNo;

    /**
     * 
     */
    private float payPrice;

    /**
     * 
     */
    private String sign;

    /**
     * 0-未支付，1-已支付
     */
    private int status;

    /**
     * 
     */
    private long userId;

    private long payTime;
    private String payTimeText;

}
