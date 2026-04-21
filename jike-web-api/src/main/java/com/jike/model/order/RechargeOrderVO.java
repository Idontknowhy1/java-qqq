package com.jike.model.order;

import lombok.Getter;
import lombok.Setter;

/**
 * 积分充值订单 VO
 */
@Getter
@Setter
public class RechargeOrderVO {

    private String id;
    private String userId;
    private String packageId;
    private Integer points;
    private Integer amountFen;
    private String codeUrl;
    private String status;
    private Long createTime;
    private Long updateTime;
    private Long paidTime;
}