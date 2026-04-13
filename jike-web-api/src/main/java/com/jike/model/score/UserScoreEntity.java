package com.jike.model.score;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_user_score") 
public class UserScoreEntity implements Serializable {
    /**
     * 
     */
    private long forScore;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private long userId;

    /**
     * 
     */
    private long vipScore;

}
