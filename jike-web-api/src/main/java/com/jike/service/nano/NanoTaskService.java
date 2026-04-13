package com.jike.service.nano;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jike.model.nano.AdminNanoTaskVO;
import com.jike.mapper.nano.NanoTaskMapper;
import com.jike.model.nano.NanoTaskEntity;
import com.jike.model.nano.NanoTaskVO;
import com.jike.model.nano.NanoTaskDTO;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.common.ConvertUtil;
import com.jjs.common.PageListResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class NanoTaskService extends AppBaseServiceV2<NanoTaskEntity, NanoTaskVO,NanoTaskDTO> {

    final NanoTaskMapper mapper;
    public NanoTaskService(NanoTaskMapper mapper) { this.mapper = mapper; } 
    public Class<NanoTaskVO> getVoClass() { return NanoTaskVO.class; }
    public Class<NanoTaskEntity> getEntityClass() { return NanoTaskEntity.class; } 

    @Override
    public NanoTaskMapper getMapper() {
        return mapper;
    }

    public PageListResult<AdminNanoTaskVO> getPageList(int pageNum, int pageSize, String uuid) throws Exception {

        //记录总条数
        Integer totalCount = mapper.adminPageCount(uuid);

        //查询结果
        List<AdminNanoTaskVO>list = mapper.adminPageList(new HashMap() {{
            put("uuid", uuid);
            put("startIndex", (pageNum - 1) * pageSize);
            put("pageSize", pageSize);
        }});


        PageListResult<AdminNanoTaskVO> pageResult = new PageListResult<AdminNanoTaskVO>();
        pageResult.setTotalCount(totalCount);
        pageResult.setPageNum(pageNum);
        pageResult.setList(list);
        return pageResult;
    }

    public PageListResult<AdminNanoTaskVO> queryPageList(int pageNum, int pageSize, QueryWrapper<AdminNanoTaskVO> queryWrapper) throws Exception {
        // 先查询总数
        long total = mapper.queryCount(queryWrapper);
        Page<AdminNanoTaskVO> page = new Page<>(pageNum, pageSize);

        // 如果总数大于0，再查询数据
        if (total > 0) {
            page.setSearchCount(false);
            List<AdminNanoTaskVO> records = mapper.queryPageList(page, queryWrapper);
            page.setRecords(records);
        } else {
            page.setRecords(Collections.emptyList());
        }

        PageListResult<AdminNanoTaskVO> pageResult = new PageListResult<AdminNanoTaskVO>();
        pageResult.setTotalCount(total);
        pageResult.setPageNum(pageNum);
        pageResult.setPageSize(pageSize);
        pageResult.setList(page.getRecords());

        return pageResult;
    }

}
