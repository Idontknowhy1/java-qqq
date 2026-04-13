package com.jike.service.bus;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.bus.Convertor_MaterialSquare;
import com.jike.mapper.bus.MaterialSquareMapper;
import com.jike.model.bus.MaterialSquareEntity;
import com.jike.model.bus.MaterialSquareVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MaterialSquareService extends ServiceImpl<BaseMapper<MaterialSquareEntity>, MaterialSquareEntity> implements IService<MaterialSquareEntity> {

    final MaterialSquareMapper mapper;
    public MaterialSquareService(MaterialSquareMapper mapper) { this.mapper = mapper; } 
    
    public PageListResult<MaterialSquareVO> getPageList(Page<MaterialSquareEntity> page, Wrapper<MaterialSquareEntity> queryWrapper) throws Exception {
        Page<MaterialSquareEntity> resultPage = mapper.selectPage(page, queryWrapper);
        List<MaterialSquareEntity> list = resultPage.getRecords();

        List<MaterialSquareVO> voList = Convertor_MaterialSquare.INSTANCE.convertToVoList(list);
        for(MaterialSquareVO vo : voList) {
            String text = DateUtil.format(new Date(vo.getCreateTime() * 1000), "yyyy-MM-dd HH:mm:ss");
            vo.setCreateTimeText(text);
        }

        PageListResult<MaterialSquareVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);
        return pageResult;
    }
    public List<MaterialSquareVO> getList(Wrapper<MaterialSquareEntity> queryWrapper) throws Exception {
        List<MaterialSquareEntity> list = mapper.selectList(queryWrapper);
        return Convertor_MaterialSquare.INSTANCE.convertToVoList(list);
    }
}
