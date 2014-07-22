/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Exposes the services for handling ProcessingSchedule entity.
 */

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

  public void savePeriod(ProcessingPeriod processingPeriod) throws ParseException {
    periodRepository.insert(processingPeriod);
  }

  public void deletePeriod(Long processingPeriodId) {
    periodRepository.delete(processingPeriodId);
  }

  public List<ProcessingPeriod> getAllPeriodsAfterDateAndPeriod(Long facilityId, Long programId, Date programStartDate, Long startingPeriodId) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = getSchedule(new Facility(facilityId), new Program(programId));
    return periodRepository.getAllPeriodsAfterDateAndPeriod(requisitionGroupProgramSchedule.getProcessingSchedule().getId(),
      startingPeriodId, programStartDate, new Date());
  }

  public List<ProcessingPeriod> getOpenPeriods(Long facilityId, Long programId, Long startingPeriodId){
    return periodRepository.getOpenPeriods(facilityId, programId, startingPeriodId);
  };

  private RequisitionGroupProgramSchedule getSchedule(Facility facility, Program program) {
    RequisitionGroup requisitionGroup = requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility);
    if (requisitionGroup == null)
      throw new DataException("error.no.requisition.group");

    return requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupAndProgram(requisitionGroup.getId(), program.getId());
  }

  public ProcessingPeriod getPeriodById(Long periodId) {
    return periodRepository.getById(periodId);
  }

  public List<ProcessingPeriod> getAllPeriodsForDateRange(Facility facility, Program program, Date startDate, Date endDate) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = getSchedule(facility, program);
    return periodRepository.getAllPeriodsForDateRange(requisitionGroupProgramSchedule.getProcessingSchedule().getId(), startDate, endDate);
  }

  public List<ProcessingPeriod> getUsedPeriodsForDateRange(Facility facility, Program program, Date startDate, Date endDate) {

    return periodRepository.getRnrPeriodsForDateRange(facility.getId(),program.getId(), startDate, endDate);
  }

  public ProcessingSchedule getByCode(String code) {
    return repository.getByCode(code);
  }

  public List<ProcessingPeriod> getAllPeriodsBefore(Long scheduleId, Date beforeDate) {
    return periodRepository.getAllPeriodsBefore(scheduleId, beforeDate);
  }

  public ProcessingPeriod getPeriodForDate(Facility facility, Program program, Date date) {
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = getSchedule(facility, program);
    return periodRepository.getPeriodForDate(requisitionGroupProgramSchedule.getProcessingSchedule().getId(), date);
  }

  public ProcessingPeriod getCurrentPeriod(Long facilityId, Long programId, Date programStartDate) {
    RequisitionGroupProgramSchedule schedule = getSchedule(new Facility(facilityId), new Program(programId));
    return periodRepository.getCurrentPeriod(schedule.getProcessingSchedule().getId(), programStartDate);
  }

  public List<ProcessingPeriod> getNPreviousPeriodsInDescOrder(ProcessingPeriod currentPeriod, Integer n) {
    return periodRepository.getNPreviousPeriods(currentPeriod, n);
  }

  public Integer findM(ProcessingPeriod period) {
    List<ProcessingPeriod> nPreviousPeriods = periodRepository.getNPreviousPeriods(period, 1);
    if (nPreviousPeriods.size() > 0) {
      return nPreviousPeriods.get(0).getNumberOfMonths();
    }
    return period.getNumberOfMonths();
  }

  public List<ProcessingPeriod> getAllPeriodsForScheduleAndYear(Long scheduleId, Long year) {
        return periodRepository.getAllPeriodsForScheduleAndYear(scheduleId, year);
    }
}
