package com.jike.mapper.nano;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jike.model.nano.AdminNanoTaskVO;
import com.jike.model.nano.NanoTaskEntity;
import com.jjs.common.AppBaseMapperV2;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface NanoTaskMapper extends AppBaseMapperV2<NanoTaskEntity> {

    List<AdminNanoTaskVO> adminPageList(HashMap hashMap) throws Exception;

    Integer adminPageCount(@Param("uuid") String uuid) throws Exception;

    List<AdminNanoTaskVO> queryPageList(IPage<AdminNanoTaskVO> page, @Param("ew") Wrapper<AdminNanoTaskVO> wrapper) throws Exception;
    long queryCount(@Param("ew") Wrapper<AdminNanoTaskVO> wrapper) throws Exception;
}