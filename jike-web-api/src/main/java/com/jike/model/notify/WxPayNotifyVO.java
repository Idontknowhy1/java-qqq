package com.jike.model.notify;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WxPayNotifyVO implements Serializable {
    /**
     * 
     */
    private String body;

    /**
     * 
     */
    private long createTime;

    /**
     * 
     */
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
