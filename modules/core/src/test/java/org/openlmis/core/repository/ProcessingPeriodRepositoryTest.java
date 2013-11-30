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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.startDate;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProcessingPeriodRepositoryTest {
  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Mock
  private ProcessingPeriodMapper mapper;

  private ProcessingPeriodRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = new ProcessingPeriodRepository(mapper);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    List<ProcessingPeriod> processingPeriodList = new ArrayList<>();
    when(mapper.getAll(123L)).thenReturn(processingPeriodList);
    List<ProcessingPeriod> periods = repository.getAll(123L);

    verify(mapper).getAll(123L);
    assertThat(periods, is(processingPeriodList));
  }

  @Test
  public void shouldGetAllPeriodsAfterAGivenDateAndPeriod() throws Exception {
    Long scheduleId = 1L;
    Long startingPeriodId = 10L;
    DateTime currentDate = new DateTime();
    Date programStartDate = currentDate.minusMonths(1).toDate();
    List<ProcessingPeriod> processingPeriods = Arrays.asList(new ProcessingPeriod());
    when(mapper.getAllPeriodsAfterDateAndPeriod(scheduleId, startingPeriodId, programStartDate, currentDate.toDate())).thenReturn(processingPeriods);

    List<ProcessingPeriod> periodList = repository.getAllPeriodsAfterDateAndPeriod(scheduleId, startingPeriodId, programStartDate, currentDate.toDate());

    assertThat(periodList, is(processingPeriods));
  }

  @Test
  public void shouldGetAllPeriodsAfterAGivenDate() throws Exception {
    Long scheduleId = 1L;
    DateTime currentDate = new DateTime();
    Date programStartDate = currentDate.minusMonths(1).toDate();
    List<ProcessingPeriod> processingPeriods = Arrays.asList(new ProcessingPeriod());
    when(mapper.getAllPeriodsAfterDate(scheduleId, programStartDate, currentDate.toDate())).thenReturn(processingPeriods);

    List<ProcessingPeriod> periodList = repository.getAllPeriodsAfterDateAndPeriod(scheduleId, null, programStartDate, currentDate.toDate());

    assertThat(periodList, is(processingPeriods));
  }

  @Test
  public void shouldInsertAPeriodForGivenSchedule() throws Exception {
    ProcessingPeriod processingPeriod = mock(ProcessingPeriod.class);
    doNothing().when(processingPeriod).validate();

    repository.insert(processingPeriod);
    verify(mapper).insert(processingPeriod);
  }

  @Test
  public void shouldNotInsertAPeriodWithSameNameForASchedule() throws Exception {
    ProcessingPeriod processingPeriod = mock(ProcessingPeriod.class);
    doNothing().when(processingPeriod).validate();
    doThrow(DuplicateKeyException.class).when(mapper).insert(processingPeriod);

    exException.expect(dataExceptionMatcher("error.period.exist.for.schedule"));

    repository.insert(processingPeriod);

  }

  @Test
  public void shouldNotInsertAPeriodWhichFailsValidation() throws Exception {
    ProcessingPeriod processingPeriod = mock(ProcessingPeriod.class);
    doThrow(new DataException("errorMsg")).when(processingPeriod).validate();

    exException.expect(DataException.class);
    exException.expectMessage("errorMsg");

    repository.insert(processingPeriod);
    verify(processingPeriod).validate();
  }

  @Test
  public void shouldNotInsertAPeriodIfItsStartDateIsSmallerThanEndDateOfPreviousPeriod() throws Exception {
    final ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    ProcessingPeriod lastAddedPeriod = make(a(defaultProcessingPeriod, with(ProcessingPeriodBuilder.endDate, processingPeriod.getStartDate())));
    when(mapper.getLastAddedProcessingPeriod(processingPeriod.getScheduleId())).thenReturn(lastAddedPeriod);
    doThrow(DataException.class).when(mapper).insert(processingPeriod);

    exException.expect(dataExceptionMatcher("error.period.start.date.less.than.last.period.end.date"));

    repository.insert(processingPeriod);
  }

  @Test
  public void shouldThrowExceptionIfStartDateLessThanCurrentDateWhenDeletingPeriod() {
    Calendar currentDate = Calendar.getInstance();
    Calendar periodStartDate = (Calendar) currentDate.clone();
    periodStartDate.add(Calendar.DATE, -1);
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(startDate, periodStartDate.getTime())));
    when(mapper.getById(processingPeriod.getId())).thenReturn(processingPeriod);

    exException.expect(dataExceptionMatcher("error.period.start.date"));

    repository.delete(processingPeriod.getId());
  }

  @Test
  public void shouldThrowExceptionIfStartDateEqualToCurrentDateWhenDeletingPeriod() {
    Calendar periodStartDate = Calendar.getInstance();
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(startDate, periodStartDate.getTime())));
    when(mapper.getById(processingPeriod.getId())).thenReturn(processingPeriod);

    exException.expect(dataExceptionMatcher("error.period.start.date"));

    repository.delete(processingPeriod.getId());
  }

  @Test
  public void shouldDeleteAPeriodIfStartDateGreaterThanCurrentDate() {
    Calendar currentDate = Calendar.getInstance();
    Calendar periodStartDate = (Calendar) currentDate.clone();
    periodStartDate.add(Calendar.DATE, 1);
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(startDate, periodStartDate.getTime())));
    when(mapper.getById(processingPeriod.getId())).thenReturn(processingPeriod);

    repository.delete(processingPeriod.getId());

    verify(mapper).delete(processingPeriod.getId());
  }

  @Test
  public void shouldGetPeriodById() throws Exception {
    final ProcessingPeriod expected = new ProcessingPeriod();
    when(mapper.getById(1L)).thenReturn(expected);
    final ProcessingPeriod actual = repository.getById(1L);
    verify(mapper).getById(1L);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAllPeriodsInDateRange() throws Exception {
    Date startDate = DateTime.now().toDate();
    Date endDate = DateTime.now().toDate();
    List<ProcessingPeriod> expected = new ArrayList<>();
    Long scheduleId = 1L;
    when(mapper.getAllPeriodsForDateRange(scheduleId, startDate, endDate)).thenReturn(expected);

    List<ProcessingPeriod> actual = repository.getAllPeriodsForDateRange(scheduleId, startDate, endDate);
    verify(mapper).getAllPeriodsForDateRange(scheduleId, startDate, endDate);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetProcessingPeriodsBeforeGivenDate() throws Exception {
    Date date = new Date();
    List<ProcessingPeriod> expectedPeriods = new ArrayList<>();
    when(mapper.getAllPeriodsBefore(1l, date)).thenReturn(expectedPeriods);

    List<ProcessingPeriod> returnedPeriods = repository.getAllPeriodsBefore(1l, date);

    assertThat(returnedPeriods, is(expectedPeriods));
    verify(mapper).getAllPeriodsBefore(1l, date);

  }

  @Test
  public void shouldGetCurrentPeriodForFacilityAndProgram() {
    ProcessingPeriod currentPeriod = new ProcessingPeriod();
    Date programStartDate = new Date();
    when(mapper.getCurrentPeriod(3L, programStartDate)).thenReturn(currentPeriod);

    ProcessingPeriod expectedPeriod = repository.getCurrentPeriod(3L, programStartDate);
    verify(mapper).getCurrentPeriod(3L, programStartDate);

    assertThat(expectedPeriod, is(currentPeriod));
  }

}
