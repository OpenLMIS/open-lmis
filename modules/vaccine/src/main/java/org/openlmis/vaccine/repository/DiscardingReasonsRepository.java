package org.openlmis.vaccine.repository;

import org.openlmis.vaccine.domain.DiscardingReason;
import org.openlmis.vaccine.repository.mapper.DiscardingReasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscardingReasonsRepository {

  @Autowired
  private DiscardingReasonMapper mapper;

  public List<DiscardingReason> getAllReasons(){
    return mapper.getAll();
  }

}
