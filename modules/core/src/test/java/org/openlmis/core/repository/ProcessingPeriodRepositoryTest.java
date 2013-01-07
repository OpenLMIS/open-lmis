package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingPeriodRepositoryTest {

  @Rule
  public ExpectedException exException = ExpectedException.none();

  private ProcessingPeriodRepository repository;

  @Mock
  ProcessingPeriodMapper mapper;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
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

    verify(mapper).insert(processingPeriod);
  }

  @Test
  public void shouldNotInsertAPeriodWhichFailsValidation() throws Exception {
    ProcessingPeriod processingPeriod = mock(ProcessingPeriod.class);
    doThrow(new RuntimeException("errorMsg")).when(processingPeriod).validate();

    exException.expect(RuntimeException.class);
    exException.expectMessage("errorMsg");

    repository.insert(processingPeriod);
    verify(processingPeriod).validate();
  }
}
