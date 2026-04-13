package com.jike.service.person;

import com.jjs.common.AppBaseServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import com.jike.mapper.person.PersonMapper;
import com.jike.model.person.PersonEntity;
import com.jike.model.person.PersonVO;
import com.jike.model.person.PersonDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonService extends AppBaseServiceV2<PersonEntity, PersonVO,PersonDTO> {

    final PersonMapper mapper;
    public PersonService(PersonMapper mapper) { this.mapper = mapper; } 
    public Class<PersonVO> getVoClass() { return PersonVO.class; }
    public Class<PersonEntity> getEntityClass() { return PersonEntity.class; } 

    @Override
    public PersonMapper getMapper() {
        return mapper;
    }
    
}
