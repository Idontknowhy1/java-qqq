package com.jike.model.score;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("tb_user_score_records") 
public class UserScoreRecordEntity implements Serializable {
    /**
     * 
     */
    @TableField(updateStrategy = FieldStrategy.NEVER) 
    private long createTime = System.currentTimeMillis() / 1000;

    /**
     * 
     */
    @TableId(type = IdType.AUTO) 
    private int id;

    /**
     * 
     */
    private String memo;

    private long vipScore;
    private long forScore;
    private long vipBalanceScore;
    private long forBalanceScore;

    private String  type;

    /**
     * 
     */
    private long userId;

}
