package com.jike.service.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.base.Convertor_Banner;
import com.jike.mapper.base.BannerMapper;
import com.jike.model.base.BannerEntity;
import com.jike.model.base.BannerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BannerService extends ServiceImpl<BaseMapper<BannerEntity>, BannerEntity> implements IService<BannerEntity> {

    final BannerMapper mapper;
    public BannerService(BannerMapper mapper) { this.mapper = mapper; }
    public PageListResult<BannerVO> getPageList(Page<BannerEntity> page, Wrapper<BannerEntity> queryWrapper) throws Exception {
        Page<BannerEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<BannerEntity> list = resultPage.getRecords();
        List<BannerVO> voList = Convertor_Banner.INSTANCE.convertToVoList(list);
        PageListResult<BannerVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<BannerVO> getList(Wrapper<BannerEntity> queryWrapper) throws Exception {
        List<BannerEntity> list = mapper.selectList(queryWrapper);
        return Convertor_Banner.INSTANCE.convertToVoList(list);
    }
}
