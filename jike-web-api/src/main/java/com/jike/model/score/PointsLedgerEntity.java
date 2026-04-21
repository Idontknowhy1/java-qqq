package com.jike.model.score;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 积分账本实体
 */
@Getter
@Setter
@TableName("points_ledger")
public class PointsLedgerEntity implements Serializable {

    /**
     * 记录ID
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 关联订单号
     */
    private String orderId;

    /**
     * 积分数量（正数）
     */
    private Integer points;

    /**
     * 创建时间（毫秒）
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Long createTime;
}