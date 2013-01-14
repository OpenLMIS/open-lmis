package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
@NoArgsConstructor
public class ProcessingPeriodRepository {

  private ProcessingPeriodMapper mapper;
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  @Autowired
  public ProcessingPeriodRepository(ProcessingPeriodMapper processingPeriodMapper, RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository) {
    this.mapper = processingPeriodMapper;
    this.requisitionGroupProgramScheduleRepository = requisitionGroupProgramScheduleRepository;
  }

  public List<ProcessingPeriod> getAll(Integer scheduleId) {
    return mapper.getAll(scheduleId);
  }

  public void insert(ProcessingPeriod processingPeriod) {
    processingPeriod.validate();
    try {
      validateStartDateGreaterThanLastPeriodEndDate(processingPeriod);
      mapper.insert(processingPeriod);
    } catch (DuplicateKeyException e) {
      throw new DataException("Period Name already exists for this schedule");
    }
  }


  private void validateStartDateGreaterThanLastPeriodEndDate(ProcessingPeriod processingPeriod) {
    ProcessingPeriod lastAddedProcessingPeriod = mapper.getLastAddedProcessingPeriod(processingPeriod.getScheduleId());
    if (lastAddedProcessingPeriod != null && lastAddedProcessingPeriod.getEndDate().compareTo(processingPeriod.getStartDate()) >= 0)
      throw new DataException("Period's Start Date is smaller than Previous Period's End Date");
  }

  public void delete(Integer processingPeriodId) {
    ProcessingPeriod processingPeriod = mapper.getById(processingPeriodId);
    validateStartDateGreaterThanCurrentDate(processingPeriod);
    mapper.delete(processingPeriodId);
  }

  private void validateStartDateGreaterThanCurrentDate(ProcessingPeriod processingPeriod) {
    if (processingPeriod.getStartDate().compareTo(new Date()) <= 0) {
      throw new DataException("Period's Start Date is smaller than Current Date");
    }
  }

  public List<ProcessingPeriod> getAllPeriodsForARequisitionGroupAndAProgram(Integer requisitionGroupId, Integer programId) {
    Integer scheduleId = requisitionGroupProgramScheduleRepository.getScheduleIdForRequisitionGroupAndProgram(requisitionGroupId, programId);
    return mapper.getAll(scheduleId);
  }
}