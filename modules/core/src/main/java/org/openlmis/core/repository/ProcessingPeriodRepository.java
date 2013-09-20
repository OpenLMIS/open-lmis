/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

  public ProcessingPeriod getById(Long id) {
    return mapper.getById(id);
  }

  public ProcessingPeriod getImmediatePreviousPeriod(ProcessingPeriod period) {
    return mapper.getImmediatePreviousPeriodFor(period);
  }

  public List<ProcessingPeriod> getAllPeriodsForDateRange(Long scheduleId, Date startDate, Date endDate) {
    return mapper.getAllPeriodsForDateRange(scheduleId, startDate, endDate);
  }

  public List<ProcessingPeriod> getAllPeriodsBefore(Long scheduleId, Date beforeDate) {
    return mapper.getAllPeriodsBefore(scheduleId, beforeDate);
  }

  public ProcessingPeriod getCurrentPeriod(Long scheduleId) {
    return mapper.getCurrentPeriod(scheduleId);
  }
}