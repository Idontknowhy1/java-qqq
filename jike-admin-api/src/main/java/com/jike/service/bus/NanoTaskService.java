package com.jike.service.bus;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jjs.common.PageListResult;
import com.jike.mapper.bus.NanoTaskMapper;
import com.jike.model.bus.NanoTaskEntity;
import com.jike.model.bus.NanoTaskVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class NanoTaskService extends ServiceImpl<BaseMapper<NanoTaskEntity>, NanoTaskEntity> implements IService<NanoTaskEntity> {

    final NanoTaskMapper mapper;
    public NanoTaskService(NanoTaskMapper mapper) { this.mapper = mapper; } 

    public PageListResult<NanoTaskVO> getPageList(int pageNum, int pageSize, String uuid) throws Exception {

        //记录总条数
        Integer totalCount = mapper.adminPageCount(uuid);

        //查询结果
        List<NanoTaskVO>list = mapper.adminPageList(new HashMap() {{
            put("uuid", uuid);
            put("startIndex", (pageNum - 1) * pageSize);
            put("pageSize", pageSize);
        }});


        PageListResult<NanoTaskVO> pageResult = new PageListResult<>();
        pageResult.setTotalCount(totalCount);
        pageResult.setPageNum(pageNum);
        pageResult.setList(list);
        return pageResult;
    }
}
