package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@NoArgsConstructor
public class ProcessingPeriodRepository {

  ProcessingPeriodMapper mapper;

  @Autowired
  public ProcessingPeriodRepository(ProcessingPeriodMapper mapper) {
    this.mapper = mapper;
  }

  public List<ProcessingPeriod> getAll(Integer scheduleId) {
    return mapper.getAll(scheduleId);
  }
}
