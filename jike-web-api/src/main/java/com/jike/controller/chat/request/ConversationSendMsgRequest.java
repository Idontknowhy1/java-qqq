package com.jike.controller.chat.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConversationSendMsgRequest {
    @NotBlank
    String content;

    @NotBlank
    String convId;
}
