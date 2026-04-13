package com.jike.request;

import lombok.Data;
import org.apache.http.util.TextUtils;

@Data
public class AppRequestHeader {
    private String token = "";
    private String  userId = "";

    public boolean isValid() {
        return !TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId);
    }
}
