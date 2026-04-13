package com.jike.model.notify;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_wx_pay_notifies") 
public class WxPayNotifyEntity implements Serializable {
    /**
     * 
     */
    private String body;

    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private String orderno;

    /**
     * 
     */
    private String transid;

    /**
     * 
     */
    private long userid;

}
