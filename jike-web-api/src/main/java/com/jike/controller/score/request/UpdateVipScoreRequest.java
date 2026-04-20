package com.jike.controller.score.request;

import lombok.Data;

/**
 * 更新用户 VIP 积分请求体
 */
@Data
public class UpdateVipScoreRequest {
    /**
     * 用户 ID
     */
    private long userId;

    /**
     * VIP 积分变化量
     * 正数表示增加，负数表示减少
     */
    private long vipScoreDelta;
}
