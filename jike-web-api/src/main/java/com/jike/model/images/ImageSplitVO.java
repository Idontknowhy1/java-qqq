package com.jike.model.images;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ImageSplitVO implements Serializable {
    /**
     * 
     */
    private long createTime;
    private String createTimeText;

    /**
     * 
     */
    private String detail;

    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String imageUrl;

    /**
     * 
     */
    private String resultUrl;

    /**
     * 0-待审核,1-制作中，2-已完成，3-驳回
     */
    private int state;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private long userId;

    private String reason;

}
