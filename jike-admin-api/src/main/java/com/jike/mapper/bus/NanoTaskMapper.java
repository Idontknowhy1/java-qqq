package com.jike.mapper.bus;
import com.jike.model.bus.NanoTaskVO;
import com.jjs.common.AppBaseMapperV2;
import com.jike.model.bus.NanoTaskEntity;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface NanoTaskMapper extends AppBaseMapperV2<NanoTaskEntity> {
    List<NanoTaskVO> adminPageList(HashMap hashMap) throws Exception;

    Integer adminPageCount(@Param("uuid") String uuid) throws Exception;
}