package com.jike.service.bus;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.model.bus.Convertor_Material;
import com.jike.mapper.bus.MaterialMapper;
import com.jike.model.bus.MaterialEntity;
import com.jike.model.bus.MaterialVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class MaterialService extends ServiceImpl<BaseMapper<MaterialEntity>, MaterialEntity> implements IService<MaterialEntity> {

    final MaterialMapper mapper;
    public MaterialService(MaterialMapper mapper) { this.mapper = mapper; }

    public PageListResult<MaterialVO> getPageList(int pageNum, int pageSize, int categoryId) throws Exception {

        //记录总条数
        Integer totalCount = mapper.pageCount(new HashMap<>() {{
            put("categoryId", categoryId);
        }});

        //查询结果
        List<MaterialVO> list = mapper.pageList(new HashMap<>() {{
            put("startIndex", (pageNum - 1) * pageSize);
            put("pageSize", pageSize);
            put("categoryId", categoryId);
        }});

        PageListResult<MaterialVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(totalCount);
        pageResult.setPageNum(pageNum);
        pageResult.setList(list);
        return pageResult;
    }
}
