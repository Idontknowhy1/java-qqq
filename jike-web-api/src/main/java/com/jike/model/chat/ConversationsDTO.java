package com.jike.model.chat;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ConversationsDTO implements Serializable {
    /**
     * 
     */
    private String icon;

    /**
     * 
     */
    private String title;

    @NotBlank
    private String type;

}
