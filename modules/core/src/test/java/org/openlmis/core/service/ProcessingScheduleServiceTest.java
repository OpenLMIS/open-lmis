package org.openlmis.core.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.RequisitionGroupBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.openlmis.core.repository.ProcessingScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupProgramScheduleRepository;
import org.openlmis.core.repository.RequisitionGroupRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;

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
  private final int PROCESSING_PERIOD_ID = 1;

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
    when(processingScheduleRepository.get(1)).thenReturn(processingSchedule);

    ProcessingSchedule expectedProcessingSchedule = service.get(1);

    assertThat(processingSchedule, is(expectedProcessingSchedule));
  }

  @Test
  public void shouldThrowExceptionIfScheduleNotFound() throws Exception {
    doThrow(new DataException("Schedule not found")).when(processingScheduleRepository).get(1);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Schedule not found");
    service.get(1);
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
    processingSchedule.setId(1);
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
    when(periodRepository.getAll(123)).thenReturn(periodList);

    assertThat(service.getAllPeriods(123), is(periodList));
    verify(periodRepository).getAll(123);
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
    Integer requisitionGroupId = 1;
    Integer programId = 2;
    Integer facilityId = 3;
    Integer scheduleId = 4;
    Integer startingPeriodId = 5;
    Date programStartDate = new DateTime().toDate();
    List<ProcessingPeriod> periodList = Arrays.asList(make(a(defaultProcessingPeriod)));

    RequisitionGroup requisitionGroup = make(a(RequisitionGroupBuilder.defaultRequisitionGroup));
    requisitionGroup.setId(requisitionGroupId);

    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(new Program(programId), new Facility(facilityId))).thenReturn(requisitionGroup);
    when(requisitionGroupProgramScheduleRepository.getScheduleIdForRequisitionGroupAndProgram(requisitionGroupId, programId)).thenReturn(scheduleId);
    when(periodRepository.getAllPeriodsAfterDateAndPeriod(any(Integer.class), any(Integer.class), any(Date.class), any(Date.class))).thenReturn(periodList);

    List<ProcessingPeriod> periods = service.getAllPeriodsAfterDateAndPeriod(facilityId, programId, programStartDate, startingPeriodId);

    assertThat(periods, is(periodList));
  }

  @Test
  public void shouldThrowExceptionWhenNoRequisitionGroupExistsForAFacilityAndProgram() throws Exception {
    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(new Program(1), new Facility(2))).thenReturn(null);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(ProcessingScheduleService.NO_REQUISITION_GROUP_ERROR);

    service.getAllPeriodsAfterDateAndPeriod(1, 2, null, null);
  }

  @Test
  public void shouldGetPeriodById() throws Exception {
    final ProcessingPeriod expectedPeriod = new ProcessingPeriod();
    when(periodRepository.getById(1)).thenReturn(expectedPeriod);

    final ProcessingPeriod actual = service.getPeriodById(1);

    verify(periodRepository).getById(1);
    assertThat(actual, is(expectedPeriod));
  }

  @Test
  public void shouldGetImmediatePreviousPeriod() throws Exception {
    ProcessingPeriod expected = new ProcessingPeriod();
    ProcessingPeriod period = new ProcessingPeriod(1);
    when(periodRepository.getImmediatePreviousPeriod(period)).thenReturn(expected);

    ProcessingPeriod immediatePreviousPeriod = service.getImmediatePreviousPeriod(period);

    verify(periodRepository).getImmediatePreviousPeriod(period);
    assertThat(immediatePreviousPeriod, is(expected));
  }

  @Test
  public void shouldGetAllPeriodsInDateRange() throws Exception {
    Date startDate = DateTime.now().toDate();
    Date endDate = DateTime.now().toDate();
    List<ProcessingPeriod> expected = new ArrayList<>();

    Integer scheduleId = 1;
    Facility facility  = new Facility(1);
    Program program  = new Program(2);
    RequisitionGroup requisitionGroup = new RequisitionGroup();
    requisitionGroup.setId(1);

    when(requisitionGroupRepository.getRequisitionGroupForProgramAndFacility(program, facility)).thenReturn(requisitionGroup);
    when(requisitionGroupProgramScheduleRepository.getScheduleIdForRequisitionGroupAndProgram(requisitionGroup.getId(), program.getId())).thenReturn(scheduleId);
    when(periodRepository.getAllPeriodsForDateRange(scheduleId, startDate, endDate)).thenReturn(expected);

    List<ProcessingPeriod> actual = service.getAllPeriodsForDateRange(facility, program, startDate, endDate);

    verify(periodRepository).getAllPeriodsForDateRange(scheduleId, startDate, endDate);
    assertThat(actual, is(expected));
  }
}
