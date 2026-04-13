package com.jjs.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PageListResult<T> {
    long totalCount = 0;
    long pageNum = 0;
    long pageSize = 0;
    List<T> list;
}
