package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.startDate;

public class ProcessingPeriodRepositoryTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

  private ProcessingPeriodRepository repository;

  @Mock
  private ProcessingPeriodMapper mapper;

  @Mock
  private RequisitionGroupProgramScheduleRepository requisitionGroupProgramScheduleRepository;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new ProcessingPeriodRepository(mapper, requisitionGroupProgramScheduleRepository);
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
  public void shouldGetAllPeriodsForARequisitionGroupAndProgram() throws Exception {
    List<ProcessingPeriod> processingPeriodList = new ArrayList<>();
    when(requisitionGroupProgramScheduleRepository.getScheduleIdForRequisitionGroupAndProgram(1, 2)).thenReturn(123);
    when(mapper.getAll(123)).thenReturn(processingPeriodList);
    List<ProcessingPeriod> periods = repository.getAllPeriodsForARequisitionGroupAndAProgram(1, 2);

    verify(mapper).getAll(123);
    assertThat(periods, is(processingPeriodList));
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
}
