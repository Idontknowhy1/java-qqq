package com.jike.common.model.score;

/**
 * 积分类型
 */
public enum UserScoreUpdateEnum {
    /**
     *  消费
     */
    CONSUME("CONSUME"),
    /**
     *  每日签到
     */
    DAILY_SIGN("DAILY_SIGN"),
    /**
     *  积分购买
     */
    BUY_SCORE("BUY_SCORE"),
    /**
     *  积分重置
     */
    RESET("RESET"),
    /**
     * 购买会员时初始化
     */
    INIT("INIT"),
    /**
     * 购买会员
     */
    BUY_VIP("BUY_VIP"),
    /**
     * 管理员添加积分
     */
    ADMIN_ADD_SCORE("ADMIN_ADD_SCORE"),
    /**
     * 管理员添加VIP
     */
    ADMIN_ADD_VIP("ADMIN_ADD_VIP"),
    /**
     * 系统清零
     */
    CLEAR("CLEAR"),
    /**
     * 会员过期
     */
    VIP_EXPIRED("VIP_EXPIRED"),
    ;

    private final String  value;
    UserScoreUpdateEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
