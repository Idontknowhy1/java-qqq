package com.jjs.javacode;

import com.jjs.gen.code.TableFieldInfo;

import java.util.HashMap;
import java.util.List;

public interface TableFieldInfoMapper {
    List<TableFieldInfo> getFieldList(HashMap hashMap) throws Exception;
}
