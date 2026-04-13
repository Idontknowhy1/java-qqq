package com.jike.mapper.bus;
import com.jike.model.bus.MaterialVO;
import com.jjs.common.AppBaseMapperV2;
import com.jike.model.bus.MaterialEntity;

import java.util.HashMap;
import java.util.List;

public interface MaterialMapper extends AppBaseMapperV2<MaterialEntity> {
    List<MaterialVO> pageList(HashMap hashMap) throws Exception;

    Integer pageCount(HashMap hashMap) throws Exception;
}