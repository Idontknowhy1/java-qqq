package com.jike.model.score;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserScoreRecordVO implements Serializable {
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
    private String memo;

    /**
     * 
     */
    private int score;

    /**
     * 0-支出，1-收入，2-重新分配
     */
    private int type;

    /**
     * 
     */
    private long userId;

}
