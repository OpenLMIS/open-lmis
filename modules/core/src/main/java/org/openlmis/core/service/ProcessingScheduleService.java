package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ProcessingScheduleService {
  private ProcessingScheduleRepository repository;
  private ProcessingPeriodRepository periodRepository;

  @Autowired
  public ProcessingScheduleService(ProcessingScheduleRepository scheduleRepository, ProcessingPeriodRepository periodRepository) {
    this.repository = scheduleRepository;
    this.periodRepository = periodRepository;
  }

  public List<ProcessingSchedule> getAll() {
    return repository.getAll();
  }

  public ProcessingSchedule save(ProcessingSchedule processingSchedule) {
    if (processingSchedule.getId() == null || processingSchedule.getId() == 0) {
      repository.create(processingSchedule);
    } else {
      repository.update(processingSchedule);
    }
    return repository.get(processingSchedule.getId());
  }

  public List<ProcessingPeriod> getAllPeriods(int scheduleId) {
    return periodRepository.getAll(scheduleId);
  }

  public ProcessingSchedule get(Integer id) {
    ProcessingSchedule processingSchedule = repository.get(id);
    if(processingSchedule == null) throw  new DataException("Schedule not found");
    return processingSchedule;
  }

  public void savePeriod(ProcessingPeriod processingPeriod) {
    periodRepository.insert(processingPeriod);
  }


  public void deletePeriod(Integer processingPeriodId) {
    periodRepository.delete(processingPeriodId);
  }
}
