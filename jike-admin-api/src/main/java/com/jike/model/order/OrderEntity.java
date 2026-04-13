package com.jike.model.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_orders") 
public class OrderEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

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
    @TableId(type = IdType.AUTO) 
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

    /**
     * 物料是否已经分配
     */
    private boolean assigned;

}
