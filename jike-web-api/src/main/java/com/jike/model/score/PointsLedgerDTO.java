package com.jike.model.score;

import lombok.Getter;
import lombok.Setter;

/**
 * 积分账本 DTO
 */
@Getter
@Setter
public class PointsLedgerDTO {

    private String id;
    private String userId;
    private String orderId;
    private Integer points;
    private Long createTime;
}