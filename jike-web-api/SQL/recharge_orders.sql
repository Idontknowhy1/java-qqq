-- =============================================
-- 积分充值订单表
-- =============================================
CREATE TABLE IF NOT EXISTS `recharge_orders` (
    `id` VARCHAR(64) NOT NULL COMMENT '订单号（out_trade_no）',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `package_id` VARCHAR(32) NOT NULL COMMENT '套餐ID：pkg_500, pkg_2000, pkg_5000, pkg_12000',
    `points` INT NOT NULL COMMENT '积分数量',
    `amount_fen` INT NOT NULL DEFAULT 1 COMMENT '金额（分）',
    `code_url` VARCHAR(512) DEFAULT NULL COMMENT '微信支付二维码链接',
    `status` VARCHAR(16) NOT NULL DEFAULT 'CREATED' COMMENT '订单状态：CREATED-创建,PAID-已支付,CLOSED-已关闭',
    `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒）',
    `update_time` BIGINT NOT NULL COMMENT '更新时间（毫秒）',
    `paid_time` BIGINT DEFAULT NULL COMMENT '支付时间（毫秒）',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分充值订单表';

-- =============================================
-- 积分账本表（充值成功后记录）
-- =============================================
CREATE TABLE IF NOT EXISTS `points_ledger` (
    `id` VARCHAR(64) NOT NULL COMMENT '记录ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `order_id` VARCHAR(64) NOT NULL COMMENT '关联订单号',
    `points` INT NOT NULL COMMENT '积分数量（正数）',
    `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒）',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分账本表';

-- =============================================
-- 充值套餐配置（仅供参考，实际业务中可能硬编码）
-- =============================================
-- pkg_500   -> 500积分, 1分
-- pkg_2000  -> 2000积分, 1分
-- pkg_5000  -> 5000积分, 1分
-- pkg_12000 -> 12000积分, 1分