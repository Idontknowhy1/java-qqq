package com.jjs.response;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class ApiResponse {
    private Integer code;
    private String msg;
    private Object data;
    public ApiResponse() {
        this.data = new HashMap<String,Object>();
    }

    public static ApiResponse of(Object data) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(data);
        return apiResponse;
    }
}
