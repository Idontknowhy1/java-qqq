package com.jike.service.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jike.mapper.base.DictsDetailMapper;
import com.jike.model.base.*;
import com.jjs.common.PageListResult;
import com.jike.mapper.base.DictsTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DictsService extends ServiceImpl<BaseMapper<DictsTypeEntity>, DictsTypeEntity> implements IService<DictsTypeEntity> {

    @Autowired
    DictsTypeMapper typeMapper;
    @Autowired
    DictsDetailMapper detailMapper;

    public List<DictsTypeVO> getTypeList(Wrapper<DictsTypeEntity> queryWrapper) throws Exception {
        List<DictsTypeEntity> list = typeMapper.selectList(queryWrapper);
        return Convertor_DictsType.INSTANCE.convertToVoList(list);
    }

    public List<DictsDetailVO> getDetailList(Wrapper<DictsDetailEntity> queryWrapper) throws Exception {
        List<DictsDetailEntity> list = detailMapper.selectList(queryWrapper);
        return Convertor_DictsDetail.INSTANCE.convertToVoList(list);
    }
}
