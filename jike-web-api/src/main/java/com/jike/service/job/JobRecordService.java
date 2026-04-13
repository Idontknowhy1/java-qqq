package com.jike.service.job;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.job.JobRecordMapper;
import com.jike.model.job.JobRecordEntity;
import com.jike.model.job.JobRecordVO;
import com.jike.model.job.JobRecordDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobRecordService extends AppBaseServiceV2<JobRecordEntity, JobRecordVO,JobRecordDTO> {

    final JobRecordMapper mapper;
    public JobRecordService(JobRecordMapper mapper) { this.mapper = mapper; } 
    public Class<JobRecordVO> getVoClass() { return JobRecordVO.class; }
    public Class<JobRecordEntity> getEntityClass() { return JobRecordEntity.class; } 

    @Override
    public JobRecordMapper getMapper() {
        return mapper;
    }
    
}
