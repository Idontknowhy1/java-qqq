package com.jike.controller.bus.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageSplitUpdateStateRequest {

    long id;
    String state;
    String resultUrl;
    String reason;

}
