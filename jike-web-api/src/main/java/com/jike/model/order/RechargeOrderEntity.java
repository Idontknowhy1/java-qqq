package com.jike.model.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 积分充值订单实体
 */
@Getter
@Setter
@TableName("recharge_orders")
public class RechargeOrderEntity implements Serializable {

    /**
     * 订单号（out_trade_no）
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 套餐ID：pkg_500, pkg_2000, pkg_5000, pkg_12000
     */
    private String packageId;

    /**
     * 积分数量
     */
    private Integer points;

    /**
     * 金额（分）
     */
    private Integer amountFen;

    /**
     * 微信支付二维码链接
     */
    private String codeUrl;

    /**
     * 订单状态：CREATED-创建,PAID-已支付,CLOSED-已关闭
     */
    private String status;

    /**
     * 创建时间（毫秒）
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long createTime;

    /**
     * 更新时间（毫秒）
     */
    private Long updateTime;

    /**
     * 支付时间（毫秒）
     */
    private Long paidTime;
}