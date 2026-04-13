package com.jike.model.nano;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AdminNanoTaskVO implements Serializable {
    /**
     * 
     */
    private long createTime;
    private String createTimeText;


    /**
     * 
     */
    private long finishTime;

    private long id;
    private String idText;

    private String nickname;

    /**
     * 
     */
    private String prompt;

    /**
     * 
     */
    private String referImages;

    /**
     * 
     */
    private String resultImages;

    /**
     * 
     */
    private String size;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private long userId;

    private String uuid;

    private String hpResultImage;
    private int hpStatus;

    private String model;

}
