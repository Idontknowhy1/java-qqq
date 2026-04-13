package com.jike.model.job;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class JobRecordVO implements Serializable {
    /**
     * 
     */
    private String env;

    /**
     * 
     */
    private Date executeTime;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String name;

}
