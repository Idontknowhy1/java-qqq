package com.jike.model.score;

import lombok.Getter;
import lombok.Setter;

/**
 * 积分账本 VO
 */
@Getter
@Setter
public class PointsLedgerVO {

    private String id;
    private String userId;
    private String orderId;
    private Integer points;
    private Long createTime;
}