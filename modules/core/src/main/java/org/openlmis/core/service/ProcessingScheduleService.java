/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
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

  public List<ProcessingPeriod> getAllPeriods(Long scheduleId) {
    return periodRepository.getAll(scheduleId);
  }

  public ProcessingSchedule get(Long id) {
    ProcessingSchedule processingSchedule = repository.get(id);
    if (processingSchedule == null) {
      throw new DataException("error.schedule.not.found");
    }
    return processingSchedule;
  }

  public void savePeriod(ProcessingPeriod processingPeriod) {
    periodRepository.insert(processingPeriod);
  }

  public void deletePeriod(Long processingPeriodId) {
    periodRepository.delete(processingPeriodId);
  }

  public List<ProcessingPeriod> getAllPeriodsAfterDateAndPeriod(Long facilityId, Long programId, Date programStartDate, Long startingPeriod) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = getSchedule(new Facility(facilityId), new Program(programId));
    return periodRepository.getAllPeriodsAfterDateAndPeriod(requisitionGroupProgramSchedule.getProcessingSchedule().getId(),
        startingPeriod, programStartDate, new Date());
  }

  private RequisitionGroupProgramSchedule getSchedule(Facility facility, Program program) {
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility);
    if (requisitionGroup == null)
      throw new DataException("error.no.requisition.group");

    return requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupAndProgram(requisitionGroup.getId(), program.getId());
  }

  public ProcessingPeriod getPeriodById(Long periodId) {
    return periodRepository.getById(periodId);
  }

  public ProcessingPeriod getImmediatePreviousPeriod(ProcessingPeriod period) {
    return periodRepository.getImmediatePreviousPeriod(period);
  }

  public List<ProcessingPeriod> getAllPeriodsForDateRange(Facility facility, Program program, Date startDate, Date endDate) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = getSchedule(facility, program);
    return periodRepository.getAllPeriodsForDateRange(requisitionGroupProgramSchedule.getProcessingSchedule().getId(), startDate, endDate);
  }

  public ProcessingSchedule getByCode(String code) {
    return repository.getByCode(code);
  }

  public List<ProcessingPeriod> getAllPeriodsBefore(Long scheduleId, Date beforeDate) {
    return periodRepository.getAllPeriodsBefore(scheduleId, beforeDate);
  }
}
