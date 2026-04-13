package com.jjs.common;

import lombok.Getter;

/**
 * 适配BaseService
 */
@Getter
public class BasePageQuery {
    protected static final int DEFAULT_PAGE_NUM = 1;
    protected static final int DEFAULT_PAGE_SIZE = 10;
    protected static final int MAX_PAGE_SIZE = 1000;

    private int pageNum = DEFAULT_PAGE_NUM;
    private int pageSize = DEFAULT_PAGE_SIZE;

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum < 1? DEFAULT_PAGE_NUM : pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (pageSize < 1) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > getMaxPageSize()) {
            this.pageSize = getMaxPageSize();
        }
    }

    public int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }

    public int getStartIndex() {
        return getPageSize() * (getPageNum() - 1);
    }
}
