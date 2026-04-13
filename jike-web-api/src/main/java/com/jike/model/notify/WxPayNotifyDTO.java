package com.jike.model.notify;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WxPayNotifyDTO implements Serializable {
    /**
     * 
     */
    private String body;

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
