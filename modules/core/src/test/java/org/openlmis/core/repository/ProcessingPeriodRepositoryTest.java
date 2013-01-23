package org.openlmis.core.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.startDate;

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
    when(mapper.getAll(123)).thenReturn(processingPeriodList);
    List<ProcessingPeriod> periods = repository.getAll(123);

    verify(mapper).getAll(123);
    assertThat(periods, is(processingPeriodList));
  }

  @Test
  public void shouldGetAllPeriodsAfterAGivenDateAndPeriod() throws Exception {
    int scheduleId = 1;
    Integer startingPeriodId = 10;
    DateTime currentDate = new DateTime();
    Date programStartDate = currentDate.minusMonths(1).toDate();
    List<ProcessingPeriod> processingPeriods = Arrays.asList(new ProcessingPeriod());
    when(mapper.getAllPeriodsAfterDateAndPeriod(scheduleId, startingPeriodId, programStartDate, currentDate.toDate())).thenReturn(processingPeriods);

    List<ProcessingPeriod> periodList = repository.getAllPeriodsAfterDateAndPeriod(scheduleId, startingPeriodId, programStartDate, currentDate.toDate());

    assertThat(periodList, is(processingPeriods));
  }

  @Test
  public void shouldGetAllPeriodsAfterAGivenDate() throws Exception {
    int scheduleId = 1;
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

    exException.expect(DataException.class);
    exException.expectMessage("Period Name already exists for this schedule");

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

    exException.expect(DataException.class);
    exException.expectMessage("Period's Start Date is smaller than Previous Period's End Date");

    repository.insert(processingPeriod);
  }

  @Test
  public void shouldThrowExceptionIfStartDateLessThanCurrentDateWhenDeletingPeriod() {
    Calendar currentDate = Calendar.getInstance();
    Calendar periodStartDate = (Calendar) currentDate.clone();
    periodStartDate.add(Calendar.DATE, -1);
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(startDate, periodStartDate.getTime())));
    when(mapper.getById(processingPeriod.getId())).thenReturn(processingPeriod);
    exException.expect(DataException.class);
    exException.expectMessage("Period's Start Date is smaller than Current Date");

    repository.delete(processingPeriod.getId());
  }

  @Test
  public void shouldThrowExceptionIfStartDateEqualToCurrentDateWhenDeletingPeriod() {
    Calendar periodStartDate = Calendar.getInstance();
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(startDate, periodStartDate.getTime())));
    when(mapper.getById(processingPeriod.getId())).thenReturn(processingPeriod);
    exException.expect(DataException.class);
    exException.expectMessage("Period's Start Date is smaller than Current Date");

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
    when(mapper.getById(1)).thenReturn(expected);
    final ProcessingPeriod actual = repository.getById(1);
    verify(mapper).getById(1);
    assertThat(actual, is(expected));
  }
}
