package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.RequisitionGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@NoArgsConstructor
public class ProcessingScheduleService {
  private ProcessingScheduleRepository repository;
  private ProcessingPeriodRepository periodRepository;
  private RequisitionGroupRepository requisitionGroupRepository;
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  @Autowired
  public ProcessingScheduleService(ProcessingScheduleRepository scheduleRepository, ProcessingPeriodRepository periodRepository,
                                   RequisitionGroupRepository requisitionGroupRepository, RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository) {
    this.repository = scheduleRepository;
    this.periodRepository = periodRepository;
    this.requisitionGroupRepository = requisitionGroupRepository;
    this.requisitionGroupProgramScheduleRepository = requisitionGroupProgramScheduleRepository;
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
    if (processingSchedule == null) throw new DataException("Schedule not found");
    return processingSchedule;
  }

  public void savePeriod(ProcessingPeriod processingPeriod) {
    periodRepository.insert(processingPeriod);
  }

  public void deletePeriod(Integer processingPeriodId) {
    periodRepository.delete(processingPeriodId);
  }

  public List<ProcessingPeriod> getAllPeriodsAfterDateAndPeriod(Integer facilityId, Integer programId, Date programStartDate, Integer startingPeriod) {
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(programId, facilityId);
    Integer scheduleId = requisitionGroupProgramScheduleRepository.getScheduleIdForRequisitionGroupAndProgram(requisitionGroup.getId(), programId);
    return periodRepository.getAllPeriodsAfterDateAndPeriod(scheduleId, startingPeriod, programStartDate, new Date());
  }
}
