package com.jjs.common;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertUtil {
    /**
     * 将单个 Entity 转换为 VO
     */
    public static <V> V convertTo(Object source, Class<V> targetClass) {
        if (source == null) {
            return null;
        }
        V target = null;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            throw new RuntimeException("Convert entity to vo failed", e);
        }
        return target;
    }

    /**
     * 将 Entity 列表转换为 VO 列表
     */
    public static <V> List<V> convertToList(Collection<?> sourceList, Class<V> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(source -> convertTo(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * PageListResult类型转换
     */
    public static <T, VOT> PageListResult<VOT> convertToPageResult(PageListResult<T> sourcePageResult, Class<VOT> voClass) {

        PageListResult<VOT> pageListResult = new PageListResult<VOT>();
        pageListResult.setTotalCount(sourcePageResult.getTotalCount());
        pageListResult.setPageNum(sourcePageResult.getPageNum());
        pageListResult.setPageSize(sourcePageResult.getPageSize());

        List<VOT> voList = new ArrayList<>();
        for (T item : sourcePageResult.getList()) {
            VOT vo = ConvertUtil.convertTo(item, voClass);
            voList.add(vo);
        }
        pageListResult.setList(voList);

        return pageListResult;
    }

    /**
     * 转换分页结果：将 Page<T> 转换为 Page<V>
     * 此方法能直接处理分页对象，保留分页信息
     */
//    public static <T, V> Page<V> convertToPage(Page<T> sourcePage, Class<V> targetVoClass) {
//        Page<V> targetPage = new Page<>();
//        // 复制分页信息
//        BeanUtils.copyProperties(sourcePage, targetPage, "records"); // 排除records，我们单独转换
//        // 转换records列表
//        List<V> voRecords = convertToList(sourcePage.getRecords(), targetVoClass);
//        targetPage.setRecords(voRecords);
//        return targetPage;
//    }
}
