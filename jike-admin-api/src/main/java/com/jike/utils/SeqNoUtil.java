package com.jike.utils;

import com.jjs.common.SnowflakeId;

public class SeqNoUtil {
    /**
     * 活动编号
     */
    public static String generateActivitySeqNo() {
        return "ACT" + SnowflakeId.generateId();
    }
    public static String generateRefundSeqNo() {
        return "REF" + SnowflakeId.generateId();
    }

}
