package com.jike.model.job;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("tb_job_records") 
public class JobRecordEntity implements Serializable {
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
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private String name;

}
