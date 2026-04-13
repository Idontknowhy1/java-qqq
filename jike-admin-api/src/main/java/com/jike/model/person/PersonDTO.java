package com.jike.model.person;

import com.baomidou.mybatisplus.annotation.*; 
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PersonDTO implements Serializable {
    /**
     * 
     */
    private int id;

    /**
     * 
     */
    private String name;

}
