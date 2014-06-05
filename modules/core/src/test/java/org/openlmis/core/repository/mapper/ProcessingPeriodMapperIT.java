/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.code;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ProcessingPeriodMapperIT {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private ProcessingPeriodMapper mapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  private ProcessingSchedule schedule;

  @Before
  public void setUp() throws Exception {
    schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);
  }

  @Test
  public void shouldReturnAllPeriodsForASchedule() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.plusMonths(3);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, date1.toDate()), with(scheduleId, schedule.getId())));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, date2.toDate()), with(scheduleId, schedule.getId()), with(name, "Month2")));

    mapper.insert(period1);
    mapper.insert(period2);

    List<ProcessingPeriod> fetchedPeriods = mapper.getAll(schedule.getId());
    assertThat(fetchedPeriods.size(), is(2));
    assertThat(fetchedPeriods.get(0).getId(), is(period2.getId()));
  }

  @Test
  public void shouldReturnEmptyListIfNoPeriodFoundForGivenSchedule() throws Exception {
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));
    mapper.insert(period1);

    List<ProcessingPeriod> fetchedPeriods = mapper.getAll(1233L);
    assertTrue(fetchedPeriods.isEmpty());
  }

  @Test
  public void shouldInsertPeriodWithAllData() throws Exception {
    Date date1 = new Date();
    Date date2 = new Date(date1.getTime() + 90 * 24 * 60 * 60 * 1000);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod,
      with(scheduleId, schedule.getId()),
      with(ProcessingPeriodBuilder.startDate, date1), with(endDate, date2), with(numberOfMonths, 3)));

    Integer insertCount = mapper.insert(period1);

    assertThat(insertCount, is(1));
    ProcessingPeriod insertedPeriod = mapper.getAll(schedule.getId()).get(0);
    assertThat(insertedPeriod.getName(), is("Month1"));
    assertThat(insertedPeriod.getDescription(), is("first month"));
    assertThat(insertedPeriod.getStartDate(), is(date1));
    assertThat(insertedPeriod.getEndDate(), is(date2));
    assertThat(insertedPeriod.getNumberOfMonths(), is(3));
    assertThat(insertedPeriod.getModifiedBy(), is(1L));
    assertThat(insertedPeriod.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldNotInsertDuplicatePeriodName() throws Exception {
    ProcessingSchedule schedule2 = make(a(defaultProcessingSchedule, with(code, "XXX")));
    scheduleMapper.insert(schedule2);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId()), with(name, "Month1")));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(scheduleId, schedule2.getId()), with(name, "Month1")));
    ProcessingPeriod period3 = make(a(defaultProcessingPeriod, with(scheduleId, schedule2.getId()), with(name, "Month1")));
    expectedException.expect(DuplicateKeyException.class);
    expectedException.expectMessage("duplicate key value violates unique constraint");
    mapper.insert(period1);
    mapper.insert(period2);
    mapper.insert(period3);
  }

  @Test
  public void shouldReturnLastAddedPeriodForASchedule() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.plusMonths(3);

    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, date1.toDate()), with(scheduleId, schedule.getId())));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, date2.toDate()), with(scheduleId, schedule.getId()), with(name, "Month2")));

    mapper.insert(period1);
    mapper.insert(period2);

    ProcessingPeriod fetchedPeriod = mapper.getLastAddedProcessingPeriod(schedule.getId());
    assertThat(fetchedPeriod.getId(), is(period2.getId()));
  }

  @Test
  public void shouldDeleteAPeriod() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));
    mapper.insert(processingPeriod);

    mapper.delete(processingPeriod.getId());
    assertThat(mapper.getAll(schedule.getId()).size(), is(0));
  }

  @Test
  public void shouldGetPeriodById() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));
    mapper.insert(processingPeriod);

    ProcessingPeriod period = mapper.getById(processingPeriod.getId());
    assertThat(period.getName(), is(processingPeriod.getName()));
  }

  @Test
  public void shouldGetAllPeriodsAfterAGivenPeriodAndADate() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);
    DateTime date3 = date1.minusMonths(2);
    DateTime date4 = date1.minusMonths(3);
    DateTime date5 = date1.minusMonths(4);
    DateTime futureDate = date1.plusMonths(1);

    insertProcessingPeriod("Period 1", date5, date4);
    ProcessingPeriod period2 = insertProcessingPeriod("Period 2", date4.plusDays(1), date3);
    ProcessingPeriod period3 = insertProcessingPeriod("Period 3", date3.plusDays(1), date2);
    ProcessingPeriod period4 = insertProcessingPeriod("Period 4", date2.plusDays(1), date1);
    insertProcessingPeriod("Period 5", date1.plusDays(1), futureDate);

    List<ProcessingPeriod> relevantPeriods = mapper.getAllPeriodsAfterDateAndPeriod(schedule.getId(), period2.getId(), date3.toDate(), date1.toDate());

    assertThat(relevantPeriods.size(), is(2));
    assertThat(relevantPeriods.get(0).getId(), is(period3.getId()));
    assertThat(relevantPeriods.get(1).getId(), is(period4.getId()));
  }

  @Test
  public void shouldGetAllPeriodsAfterAGivenDate() throws Exception {
    DateTime date1 = new DateTime();
    DateTime date2 = date1.minusMonths(1);
    DateTime date3 = date1.minusMonths(2);
    DateTime date4 = date1.minusMonths(3);
    DateTime date5 = date1.minusMonths(4);
    DateTime futureDate = date1.plusMonths(1);

    insertProcessingPeriod("Period 1", date5, date4);
    ProcessingPeriod period2 = insertProcessingPeriod("Period 2", date4.plusDays(1), date3);
    ProcessingPeriod period3 = insertProcessingPeriod("Period 3", date3.plusDays(1), date2);
    ProcessingPeriod period4 = insertProcessingPeriod("Period 4", date2.plusDays(1), date1);
    insertProcessingPeriod("Period 5", date1.plusDays(1), futureDate);

    List<ProcessingPeriod> relevantPeriods = mapper.getAllPeriodsAfterDate(schedule.getId(), date3.toDate(), date1.toDate());

    assertThat(relevantPeriods.size(), is(3));
    assertThat(relevantPeriods.get(0).getId(), is(period2.getId()));
    assertThat(relevantPeriods.get(1).getId(), is(period3.getId()));
    assertThat(relevantPeriods.get(2).getId(), is(period4.getId()));
  }

  @Test
  public void shouldGetAllPeriodsForPeriodDateRange() throws Exception {
    DateTime currentDate = DateTime.parse("2013-01-01");
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month1")));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.plusMonths(1).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(2).toDate()), with(scheduleId, schedule.getId()), with(name, "Month2")));
    ProcessingPeriod period3 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.plusMonths(2).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(3).toDate()), with(scheduleId, schedule.getId()), with(name, "Month3")));
    ProcessingPeriod period4 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.plusDays(2).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month4")));
    ProcessingPeriod period5 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.minusDays(2).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(3).toDate()), with(scheduleId, schedule.getId()), with(name, "Month5")));

    mapper.insert(period1);
    mapper.insert(period2);
    mapper.insert(period3);
    mapper.insert(period4);
    mapper.insert(period5);
    DateTime searchStartDate = currentDate;
    DateTime searchEndDate = searchStartDate.plusDays(45);
    List<ProcessingPeriod> searchResults = mapper.getAllPeriodsForDateRange(schedule.getId(), searchStartDate.toDate(), searchEndDate.toDate());
    for (ProcessingPeriod period : searchResults) {
      period.setModifiedDate(null);
    }
    assertThat(searchResults, is(hasItems(period1, period2, period4, period5)));
    assertThat(searchResults, is(not(hasItems(period3))));
  }

  @Test
  public void shouldGetAllPeriodsBeforeDate() throws Exception {
    DateTime currentDate = DateTime.parse("2013-01-01");
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month1")));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.minusMonths(2).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.minusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month2")));
    ProcessingPeriod period3 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.minusMonths(1).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month3")));
    ProcessingPeriod period4 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.plusMonths(1).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(2).toDate()), with(scheduleId, schedule.getId()), with(name, "Month5")));

    mapper.insert(period1);
    mapper.insert(period2);
    mapper.insert(period3);
    mapper.insert(period4);
    DateTime searchStartDate = currentDate;
    List<ProcessingPeriod> searchResults = mapper.getAllPeriodsBefore(schedule.getId(), searchStartDate.toDate());
    for (ProcessingPeriod period : searchResults) {
      period.setModifiedDate(null);
    }
    assertThat(searchResults, is(asList(period1, period3, period2)));
  }

  @Test
  public void shouldGetAllPeriodsBeforeCurrentDate() throws Exception {
    DateTime currentDate = DateTime.parse("2013-01-01");
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month1")));
    ProcessingPeriod period2 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.minusMonths(2).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.minusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month2")));
    ProcessingPeriod period3 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.minusMonths(1).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(1).toDate()), with(scheduleId, schedule.getId()), with(name, "Month3")));
    ProcessingPeriod period4 = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.startDate, currentDate.plusMonths(1).toDate()), with(ProcessingPeriodBuilder.endDate, currentDate.plusMonths(2).toDate()), with(scheduleId, schedule.getId()), with(name, "Month5")));

    mapper.insert(period1);
    mapper.insert(period2);
    mapper.insert(period3);
    mapper.insert(period4);
    List<ProcessingPeriod> searchResults = mapper.getAllPeriodsBefore(schedule.getId(), null);
    for (ProcessingPeriod period : searchResults) {
      period.setModifiedDate(null);
    }
    assertThat(searchResults, is(asList(period4, period1, period3, period2)));
  }

  @Test
  public void shouldGetCurrentPeriodByFacilityAndProgram() {
    Date currentDate = new Date();
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod,
      with(startDate, addDays(currentDate, -1)),
      with(endDate, addDays(currentDate, 5)),
      with(scheduleId, schedule.getId()), with(name, "Month1")));

    mapper.insert(period1);

    ProcessingPeriod actualPeriod = mapper.getCurrentPeriod(schedule.getId(), period1.getStartDate());

    assertThat(actualPeriod, is(period1));
  }

  @Test
  public void shouldGetCurrentPeriodIfProgramStartDateIsWithinCurrentPeriod() {
    Date currentDate = new Date();
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod,
      with(startDate, addDays(currentDate, -1)),
      with(endDate, addDays(currentDate, 5)),
      with(scheduleId, schedule.getId()), with(name, "Month1")));

    mapper.insert(period1);

    Date programStartDate = addDays(period1.getStartDate(), 2);
    ProcessingPeriod actualPeriod = mapper.getCurrentPeriod(schedule.getId(), programStartDate);

    assertThat(actualPeriod, is(period1));
  }

  @Test
  public void shouldNotGetCurrentPeriodIfProgramStartDateIsAfterCurrentPeriodEndDate() {
    Date currentDate = new Date();
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod,
      with(startDate, addDays(currentDate, -1)),
      with(endDate, addDays(currentDate, 5)),
      with(scheduleId, schedule.getId()), with(name, "Month1")));

    mapper.insert(period1);

    Date programStartDate = addDays(period1.getStartDate(), 7);
    ProcessingPeriod actualPeriod = mapper.getCurrentPeriod(schedule.getId(), programStartDate);

    Assert.assertNull(actualPeriod);
  }

  @Test
  public void shouldReturnNullIfCurrentPeriodDoesNotExist() {
    Date currentDate = new Date();
    ProcessingPeriod period1 = make(a(defaultProcessingPeriod,
      with(startDate, addDays(currentDate, 2)),
      with(endDate, addDays(currentDate, 5)),
      with(scheduleId, schedule.getId()), with(name, "Month1")));

    mapper.insert(period1);

    ProcessingPeriod actualPeriod = mapper.getCurrentPeriod(schedule.getId(), period1.getStartDate());

    assertNull(actualPeriod);
  }

  @Test
  public void shouldGetNPreviousPeriodsGivenSpecificPeriod() throws Exception {
    DateTime now = DateTime.now();
    insertProcessingPeriod("Period1", now.minusDays(14), now.minusDays(13));
    ProcessingPeriod period2 = insertProcessingPeriod("Period2", now.minusDays(12), now.minusDays(11));
    ProcessingPeriod period3 = insertProcessingPeriod("Period3", now.minusDays(10), now.minusDays(9));
    ProcessingPeriod period4 = insertProcessingPeriod("Period4", now.minusDays(8), now.minusDays(7));
    ProcessingPeriod period5 = insertProcessingPeriod("Period5", now.minusDays(6), now.minusDays(5));
    ProcessingPeriod period6 = insertProcessingPeriod("Period6", now.minusDays(4), now.minusDays(3));
    ProcessingPeriod currentPeriod = insertProcessingPeriod("Period", now.minusDays(2), now);

    List<ProcessingPeriod> nPreviousPeriods = mapper.getNPreviousPeriods(currentPeriod, 5);

    assertThat(nPreviousPeriods, is(asList(period6, period5, period4, period3, period2)));
    assertThat(nPreviousPeriods.size(), is(5));
  }

  @Test
  public void shouldGetPeriodContainingADateForASchedule() throws Exception {
    Date currentDate = new Date();
    ProcessingPeriod period = make(a(defaultProcessingPeriod,
      with(startDate, addDays(currentDate, -1)),
      with(endDate, addDays(currentDate, 5)),
      with(scheduleId, schedule.getId()), with(name, "Month1")));
    mapper.insert(period);

    ProcessingPeriod periodForDate = mapper.getPeriodForDate(period.getScheduleId(), currentDate);

    assertThat(periodForDate, is(period));

  }

  private ProcessingPeriod insertProcessingPeriod(String name, DateTime startDate, DateTime endDate) {
    ProcessingPeriod period = make(a(defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.name, name),
      with(scheduleId, schedule.getId()),
      with(ProcessingPeriodBuilder.startDate, startDate.toDate()),
      with(ProcessingPeriodBuilder.endDate, endDate.toDate())));

    mapper.insert(period);
    return period;
  }


}
