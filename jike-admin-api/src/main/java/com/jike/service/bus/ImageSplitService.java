package com.jike.service.bus;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.bus.Convertor_ImageSplit;
import com.jike.mapper.bus.ImageSplitMapper;
import com.jike.model.bus.ImageSplitEntity;
import com.jike.model.bus.ImageSplitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ImageSplitService extends ServiceImpl<BaseMapper<ImageSplitEntity>, ImageSplitEntity> implements IService<ImageSplitEntity> {

    final ImageSplitMapper mapper;
    public ImageSplitService(ImageSplitMapper mapper) { this.mapper = mapper; } 
    
    public PageListResult<ImageSplitVO> getPageList(Page<ImageSplitEntity> page, Wrapper<ImageSplitEntity> queryWrapper) throws Exception {
        Page<ImageSplitEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<ImageSplitEntity> list = resultPage.getRecords();
        List<ImageSplitVO> voList = Convertor_ImageSplit.INSTANCE.convertToVoList(list);
        PageListResult<ImageSplitVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<ImageSplitVO> getList(Wrapper<ImageSplitEntity> queryWrapper) throws Exception {
        List<ImageSplitEntity> list = mapper.selectList(queryWrapper);
        return Convertor_ImageSplit.INSTANCE.convertToVoList(list);
    }
}
