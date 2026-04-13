package com.jike.model.score;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserScoreRecordDTO implements Serializable {
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
