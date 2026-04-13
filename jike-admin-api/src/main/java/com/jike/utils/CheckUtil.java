package com.jike.utils;

import java.util.Arrays;

public class CheckUtil {
    //配置微信公众号时填写的Token
    private static final String token = "k7d2F2EJfM1";

    public static boolean checkSignature(String signature, String timestamp, String nonce) {

        // 拼接字符串
        String[] arr = new String[] { token, timestamp, nonce };
        // 排序
        Arrays.sort(arr);
        // 生成字符串
        StringBuffer content = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        // SHA1加密
        String tmp = DecriptUtil.SHA1(content.toString());
        return tmp.equals(signature);
    }
}
