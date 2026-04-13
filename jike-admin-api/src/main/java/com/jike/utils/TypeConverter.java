package com.jike.utils;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Named("TypeConverter")
public class TypeConverter {
    // 日期转换
    @Named("dateToString")
    public String dateToString(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    @Named("stringToDate")
    public Date stringToDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
}
