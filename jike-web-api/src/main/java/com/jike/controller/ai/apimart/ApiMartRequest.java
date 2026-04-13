package com.jike.controller.ai.apimart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiMartRequest {
    private String model;
    private ArrayList<Message> messages = new ArrayList<>();

    private Boolean stream;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}
