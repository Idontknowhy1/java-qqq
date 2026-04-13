package com.jike.controller.images.request;

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
