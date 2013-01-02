package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class ProcessingScheduleRepository {
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  public ProcessingScheduleRepository(ProcessingScheduleMapper processingScheduleMapper) {
    this.processingScheduleMapper = processingScheduleMapper;
  }

  public List<ProcessingSchedule> getAll() {
    return processingScheduleMapper.getAll();
  }

  public void create(ProcessingSchedule processingSchedule) {
    processingSchedule.validate();
    processingScheduleMapper.insert(processingSchedule);
  }

  public ProcessingSchedule get(Integer id) {
    return processingScheduleMapper.get(id);
  }

  public void update(ProcessingSchedule processingSchedule) {
    processingSchedule.validate();
    processingScheduleMapper.update(processingSchedule);
  }
}
