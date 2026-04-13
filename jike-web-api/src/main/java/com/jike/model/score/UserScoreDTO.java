package com.jike.model.score;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserScoreDTO implements Serializable {
    /**
     * 
     */
    private int forScore;

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
    private int vipScore;

}
