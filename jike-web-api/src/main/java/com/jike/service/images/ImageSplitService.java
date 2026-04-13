package com.jike.service.images;

import com.jike.mapper.images.ImageSplitMapper;
import com.jike.model.images.ImageSplitEntity;
import com.jike.model.images.ImageSplitVO;
import com.jike.model.images.ImageSplitDTO;
import com.jjs.common.AppBaseServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ImageSplitService extends AppBaseServiceV2<ImageSplitEntity, ImageSplitVO,ImageSplitDTO> {

    final ImageSplitMapper mapper;
    public ImageSplitService(ImageSplitMapper mapper) { this.mapper = mapper; } 
    public Class<ImageSplitVO> getVoClass() { return ImageSplitVO.class; }
    public Class<ImageSplitEntity> getEntityClass() { return ImageSplitEntity.class; } 

    @Override
    public ImageSplitMapper getMapper() {
        return mapper;
    }
    
}
