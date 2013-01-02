package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingScheduleRepositoryTest {

  @Rule
  public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();

  @Mock
  private ProcessingScheduleMapper processingScheduleMapper;

  private ProcessingScheduleRepository repository;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    repository = new ProcessingScheduleRepository(processingScheduleMapper);
  }

  @Test
  public void shouldGetAll() throws Exception {
    List<ProcessingSchedule> scheduleList = new ArrayList<>();
    scheduleList.add(new ProcessingSchedule());
    when(processingScheduleMapper.getAll()).thenReturn(scheduleList);

    List<ProcessingSchedule> schedules = repository.getAll();

    assertThat(schedules, is(scheduleList));
  }

  @Test
  public void shouldInsertASchedule() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testScheduleCode", "testScheduleName");
    repository.create(processingSchedule);
    verify(processingScheduleMapper).insert(processingSchedule);
  }

  @Test
  public void shouldNotSaveScheduleWithoutItsCode() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Schedule can not be saved without its code.");
    repository.create(processingSchedule);
  }

  @Test
  public void shouldNotSaveScheduleWithoutItsName() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Schedule can not be saved without its name.");
    repository.create(processingSchedule);
  }

  @Test
  public void shouldGetAScheduleById() throws Exception {
    ProcessingSchedule mockedProcessingSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleMapper.get(1)).thenReturn(mockedProcessingSchedule);
    ProcessingSchedule fetchedSchedule = repository.get(1);
    assertThat(fetchedSchedule, is(mockedProcessingSchedule));
  }

  @Test
  public void shouldUpdateAnExistingSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testScheduleCode", "testScheduleName");
    repository.update(processingSchedule);
    verify(processingScheduleMapper).update(processingSchedule);
  }

  @Test
  public void shouldNotUpdateScheduleWithoutItsCode() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Schedule can not be saved without its code.");
    repository.update(processingSchedule);
  }

  @Test
  public void shouldNotUpdateScheduleWithoutItsName() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    expectedEx.expect(RuntimeException.class);
    expectedEx.expectMessage("Schedule can not be saved without its name.");
    repository.update(processingSchedule);
  }
}
