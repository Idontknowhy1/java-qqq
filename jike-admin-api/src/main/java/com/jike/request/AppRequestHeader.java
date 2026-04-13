package com.jike.request;

import lombok.Data;

@Data
public class AppRequestHeader {
    private String token = "";
    private String  userId = "";
}
