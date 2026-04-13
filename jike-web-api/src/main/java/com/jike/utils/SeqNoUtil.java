package com.jike.utils;

import com.jjs.common.SnowflakeId;

public class SeqNoUtil {

    public static String generateOrderSeqNo() {
        return SnowflakeId.generateId();
    }
    public static String generateRefundSeqNo() {
        return "REF" + SnowflakeId.generateId();
    }

}
