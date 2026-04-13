package com.jike.utils;

import cn.dev33.satoken.stp.StpUtil;

public class SessionUtil {
    public static int userId(){
        if (StpUtil.isLogin() == false) {
            return 0;
        }
        try {
            Object userId = StpUtil.getLoginId();
            if (userId == null) {
                return 0;
            }
            return Integer.parseInt(userId.toString());
        } catch (Exception ex) {
            return 0;
        }

    }

}
