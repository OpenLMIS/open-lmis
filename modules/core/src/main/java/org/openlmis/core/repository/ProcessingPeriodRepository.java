/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * ProcessingPeriodRepository is Repository class for ProcessingPeriod related database operations.
 */

@Repository
@NoArgsConstructor
public class ProcessingPeriodRepository {

  private ProcessingPeriodMapper mapper;

  @Autowired
  public ProcessingPeriodRepository(ProcessingPeriodMapper processingPeriodMapper) {
    this.mapper = processingPeriodMapper;
  }

  public List<ProcessingPeriod> getAll(Long scheduleId) {
    return mapper.getAll(scheduleId);
  }

  public void insert(ProcessingPeriod processingPeriod) throws ParseException {
    processingPeriod.validate();
    processingPeriod.includeEntireDuration();
    try {
      validateStartDateGreaterThanLastPeriodEndDate(processingPeriod);
      mapper.insert(processingPeriod);
    } catch (DuplicateKeyException e) {
      throw new DataException("error.period.exist.for.schedule");
    }
  }

  private void validateStartDateGreaterThanLastPeriodEndDate(ProcessingPeriod processingPeriod) {
    ProcessingPeriod lastAddedProcessingPeriod = mapper.getLastAddedProcessingPeriod(processingPeriod.getScheduleId());
    if (lastAddedProcessingPeriod != null && lastAddedProcessingPeriod.getEndDate().compareTo(processingPeriod.getStartDate()) >= 0)
      throw new DataException("error.period.start.date.less.than.last.period.end.date");
  }

  public void delete(Long processingPeriodId) {
    ProcessingPeriod processingPeriod = mapper.getById(processingPeriodId);
    validateStartDateGreaterThanCurrentDate(processingPeriod);
    mapper.delete(processingPeriodId);
  }

  private void validateStartDateGreaterThanCurrentDate(ProcessingPeriod processingPeriod) {
    if (processingPeriod.getStartDate().compareTo(new Date()) <= 0) {
      throw new DataException("error.period.start.date");
    }
  }

  public List<ProcessingPeriod> getAllPeriodsAfterDateAndPeriod(Long scheduleId, Long startPeriodId, Date afterDate, Date beforeDate) {
    return startPeriodId == null ?
      mapper.getAllPeriodsAfterDate(scheduleId, afterDate, beforeDate) :
      mapper.getAllPeriodsAfterDateAndPeriod(scheduleId, startPeriodId, afterDate, beforeDate);
  }

  public List<ProcessingPeriod> getOpenPeriods(Long facilityId, Long programId, Long startingPeriodId){
    return mapper.getOpenPeriods(facilityId, programId, startingPeriodId);
  }

  public ProcessingPeriod getById(Long id) {
    return mapper.getById(id);
  }

  public List<ProcessingPeriod> getAllPeriodsForDateRange(Long scheduleId, Date startDate, Date endDate) {
    return mapper.getAllPeriodsForDateRange(scheduleId, startDate, endDate);
  }

  public List<ProcessingPeriod> getRnrPeriodsForDateRange(Long facilityId, Long programId, Date startDate, Date endDate){
    return mapper.getRnrPeriodsForDateRange(facilityId, programId, startDate, endDate);
  }

  public List<ProcessingPeriod> getAllPeriodsBefore(Long scheduleId, Date beforeDate) {
    return mapper.getAllPeriodsBefore(scheduleId, beforeDate);
  }

  public ProcessingPeriod getCurrentPeriod(Long scheduleId, Date programStartDate) {
    return mapper.getCurrentPeriod(scheduleId, programStartDate);
  }

  public List<ProcessingPeriod> getNPreviousPeriods(ProcessingPeriod currentPeriod, Integer n) {
    return mapper.getNPreviousPeriods(currentPeriod, n);
  }

  public ProcessingPeriod getPeriodForDate(Long scheduleId, Date date) {
    return mapper.getPeriodForDate(scheduleId, date);
  }

  public List<ProcessingPeriod>  getAllPeriodsForScheduleAndYear(Long scheduleId, Long year) {
      return mapper.getAllPeriodsForScheduleAndYear(scheduleId, year);
  }
}