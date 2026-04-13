package com.jike.model.score;

import lombok.Data;

@Data
public class UserScoreConsumeResult {

    boolean success;

    /**
     * 消费失败原因
     */
    String failureReason;

    /**
     * 消费记录
     */
    UserScoreRecordEntity record;
}
