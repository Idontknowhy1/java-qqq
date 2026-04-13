package com.jike.model.score;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserScoreVO implements Serializable {
    /**
     * 
     */
    private long forScore;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private long userId;

    /**
     * 
     */
    private long vipScore;


    private boolean todayResigned;

}
