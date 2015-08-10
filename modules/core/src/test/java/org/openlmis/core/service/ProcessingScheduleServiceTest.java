/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.openlmis.core.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.numberOfMonths;
import static org.openlmis.core.builder.RequisitionGroupProgramScheduleBuilder.defaultRequisitionGroupProgramSchedule;

@Category(UnitTests.class)
public class ProcessingScheduleServiceTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ProcessingScheduleRepository processingScheduleRepository;
  @Mock
  private ProcessingPeriodRepository periodRepository;
  @Mock
  private RequisitionGroupRepository requisitionGroupRepository;
  @Mock
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  private ProcessingScheduleService service;
  private final Long PROCESSING_PERIOD_ID = 1L;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new ProcessingScheduleService(processingScheduleRepository, periodRepository, requisitionGroupRepository, requisitionGroupProgramScheduleRepository);
  }

  @Test
  public void shouldGetAllSchedules() throws Exception {
    List<ProcessingSchedule> processingScheduleList = new ArrayList<>();
    processingScheduleList.add(new ProcessingSchedule());
    when(processingScheduleRepository.getAll()).thenReturn(processingScheduleList);

    List<ProcessingSchedule> processingSchedules = service.getAll();

    assertThat(processingSchedules, is(processingScheduleList));
  }

  @Test
  public void shouldGetASchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    when(processingScheduleRepository.get(1L)).thenReturn(processingSchedule);

    ProcessingSchedule expectedProcessingSchedule = service.get(1L);

    assertThat(processingSchedule, is(expectedProcessingSchedule));
  }

  @Test
  public void shouldThrowExceptionIfScheduleNotFound() throws Exception {
    doThrow(new DataException("Schedule not found")).when(processingScheduleRepository).get(1L);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Schedule not found");
    service.get(1L);
  }

  @Test
  public void shouldInsertAndReturnInsertedSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleRepository.get(processingSchedule.getId())).thenReturn(mockedSchedule);

    ProcessingSchedule returnedSchedule = service.save(processingSchedule);

    verify(processingScheduleRepository).create(processingSchedule);
    assertThat(returnedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldUpdateAndReturnUpdatedSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setId(1L);
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleRepository.get(processingSchedule.getId())).thenReturn(mockedSchedule);

    ProcessingSchedule returnedSchedule = service.save(processingSchedule);

    verify(processingScheduleRepository).update(processingSchedule);
    assertThat(returnedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldThrowErrorWhenTryingToSaveAScheduleWithNoCode() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    doThrow(new DataException("Schedule can not be saved without its code.")).when(processingScheduleRepository).create(processingSchedule);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Schedule can not be saved without its code.");

    service.save(processingSchedule);
    verify(processingScheduleRepository).create(processingSchedule);
  }

  @Test
  public void shouldThrowErrorWhenTryingToSaveAScheduleWithNoName() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    doThrow(new DataException("Schedule can not be saved without its name.")).when(processingScheduleRepository).create(processingSchedule);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Schedule can not be saved without its name.");

    service.save(processingSchedule);
    verify(processingScheduleRepository).create(processingSchedule);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    List<ProcessingPeriod> periodList = new ArrayList<>();
    when(periodRepository.getAll(123L)).thenReturn(periodList);

    assertThat(service.getAllPeriods(123L), is(periodList));
    verify(periodRepository).getAll(123L);
  }

  @Test
  public void shouldInsertAPeriod() throws Exception {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod));
    service.savePeriod(processingPeriod);
    verify(periodRepository).insert(processingPeriod);
  }

  @Test
  public void shouldDeletePeriodIfStartDateGreaterThanCurrentDate() {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(PROCESSING_PERIOD_ID);
    service.deletePeriod(processingPeriod.getId());
    verify(periodRepository).delete(processingPeriod.getId());
  }

  @Test
  public void shouldThrowExceptionIfStartDateLessThanOrEqualToCurrentDateWhenDeletingPeriod() {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(PROCESSING_PERIOD_ID);
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(periodRepository).delete(processingPeriod.getId());

    expectedException.expect(DataException.class);
    expectedException.expectMessage(errorMessage);

    service.deletePeriod(processingPeriod.getId());
  }

  @Test
  public void shouldGetAllRelevantPeriodsAfterAGivenDateAndPeriod() throws Exception {
    Long requisitionGroupId = 1L;
    Long programId = 2L;
    Long facilityId = 3L;
    Long scheduleId = 4L;
    Long startingPeriodId = 5L;
    Date programStartDate = new DateTime().toDate();
    List<ProcessingPeriod> periodList = asList(make(a(defaultProcessingPeriod)));

    RequisitionGroup requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
    requisitionGroup.setId(requisitionGroupId);

    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(new Program(programId), new Facility(facilityId))).thenReturn(requisitionGroup);
    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = make(a(defaultRequisitionGroupProgramSchedule));
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setId(scheduleId);
    requisitionGroupProgramSchedule.setProcessingSchedule(processingSchedule);
    when(requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupAndProgram(requisitionGroupId, programId)).thenReturn(requisitionGroupProgramSchedule);
    when(periodRepository.getAllPeriodsAfterDateAndPeriod(any(Long.class), any(Long.class), any(Date.class), any(Date.class))).thenReturn(periodList);

    List<ProcessingPeriod> periods = service.getAllPeriodsAfterDateAndPeriod(facilityId, programId, programStartDate, startingPeriodId);

    assertThat(periods, is(periodList));
  }

  @Test
  public void shouldThrowExceptionWhenNoRequisitionGroupExistsForAFacilityAndProgram() throws Exception {
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(new Program(1L), new Facility(2L))).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.no.requisition.group");

    service.getAllPeriodsAfterDateAndPeriod(1L, 2L, null, null);
  }

  @Test
  public void shouldGetPeriodById() throws Exception {
    final ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(periodRepository.getById(1L)).thenReturn(expectedPeriod);

    final ProcessingPeriod actual = service.getPeriodById(1L);

    verify(periodRepository).getById(1L);
    assertThat(actual, is(expectedPeriod));
  }

  @Test
  public void shouldGetAllPeriodsInDateRange() throws Exception {
    Long scheduleId = 1L;
    Date startDate = DateTime.now().toDate();
    Date endDate = DateTime.now().toDate();
    List<ProcessingPeriod> expected = new ArrayList<>();

    Facility facility = new Facility(1L);
    Program program = new Program(2L);

    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setId(1L);

    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setId(scheduleId);

    RequisitionGroupProgramSchedule requisitionGroupProgramSchedule = make(a(defaultRequisitionGroupProgramSchedule));
    requisitionGroupProgramSchedule.setProcessingSchedule(processingSchedule);

    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(requisitionGroup);
    when(requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupAndProgram(
      requisitionGroup.getId(), program.getId())).thenReturn(requisitionGroupProgramSchedule);
    when(periodRepository.getAllPeriodsForDateRange(scheduleId, startDate, endDate)).thenReturn(expected);

    List<ProcessingPeriod> actual = service.getAllPeriodsForDateRange(facility, program, startDate, endDate);

    verify(periodRepository).getAllPeriodsForDateRange(scheduleId, startDate, endDate);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetProcessingPeriodsBeforeGivenDate() throws Exception {
    Date date = new Date();
    List<ProcessingPeriod> expectedPeriods = new ArrayList<>();
    when(periodRepository.getAllPeriodsBefore(1l, date)).thenReturn(expectedPeriods);

    List<ProcessingPeriod> returnedPeriods = service.getAllPeriodsBefore(1l, date);

    assertThat(returnedPeriods, is(expectedPeriods));
    verify(periodRepository).getAllPeriodsBefore(1l, date);

  }

  @Test
  public void shouldGetCurrentPeriodForFacilityAndProgram() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule(11L);
    RequisitionGroupProgramSchedule schedule = new RequisitionGroupProgramSchedule();
    schedule.setProcessingSchedule(processingSchedule);
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setId(5L);
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(new Program(2L), new Facility(1L)))
      .thenReturn(requisitionGroup);
    when(requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupAndProgram(5L, 2L)).thenReturn(schedule);
    Date programStartDate = new Date();
    service.getCurrentPeriod(1L, 2L, programStartDate);
    verify(periodRepository).getCurrentPeriod(schedule.getProcessingSchedule().getId(), programStartDate);
  }

  @Test
  public void shouldGetNumberOfMonthsFromPreviousPeriodOfCurrentPeriod() {
    ProcessingPeriod currentPeriod = new ProcessingPeriod();
    List<ProcessingPeriod> previousPeriod = asList(make(a(defaultProcessingPeriod)));
    when(periodRepository.getNPreviousPeriods(currentPeriod, 1)).thenReturn(previousPeriod);

    Integer numberOfMonths = service.findM(currentPeriod);

    assertThat(numberOfMonths, is(1));
  }

  @Test
  public void shouldGetNumberOfMonthsFromCurrentPeriodIfPreviousDoesNotExist() {
    ProcessingPeriod currentPeriod = make(a(defaultProcessingPeriod, with(numberOfMonths, 2)));
    when(periodRepository.getNPreviousPeriods(currentPeriod, 1)).thenReturn(EMPTY_LIST);

    Integer numberOfMonths = service.findM(currentPeriod);

    assertThat(numberOfMonths, is(2));
  }

  @Test
  public void shouldGetPeriodForGivenFacilityProgramAndDate() {
    Long programId = 1L;
    Long requisitionGroupId = 2L;
    Long scheduleId = 3L;
    Program program = new Program(programId);
    Facility facility = new Facility();
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setId(requisitionGroupId);
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(requisitionGroup);
    RequisitionGroupProgramSchedule schedule = new RequisitionGroupProgramSchedule();
    ProcessingSchedule processingSchedule = new ProcessingSchedule(scheduleId);
    schedule.setProcessingSchedule(processingSchedule);
    when(requisitionGroupProgramScheduleRepository.getScheduleForRequisitionGroupAndProgram(requisitionGroupId, programId)).thenReturn(schedule);
    Date date = new Date();
    ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(periodRepository.getPeriodForDate(scheduleId, date)).thenReturn(expectedPeriod);

    ProcessingPeriod periodForDate = service.getPeriodForDate(facility, program, date);

    verify(periodRepository).getPeriodForDate(scheduleId, date);
    assertThat(periodForDate, is(expectedPeriod));
  }
}
