package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.repository.ProcessingScheduleRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingScheduleServiceTest {
  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Mock
  private ProcessingScheduleRepository processingScheduleRepository;
  private ProcessingScheduleService service;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    service = new ProcessingScheduleService(processingScheduleRepository);
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
  public void shouldInsertAndReturnInsertedSchedule() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleRepository.get(processingSchedule.getId())).thenReturn(mockedSchedule);

    ProcessingSchedule returnedSchedule = service.save(processingSchedule);

    verify(processingScheduleRepository).save(processingSchedule);
    assertThat(returnedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldThrowErrorWhenTryingToSaveAScheduleWithNoCode() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    doThrow(new RuntimeException("Schedule can not be saved without its code.")).when(processingScheduleRepository).save(processingSchedule);

    exException.expect(RuntimeException.class);
    exException.expectMessage("Schedule can not be saved without its code.");

    service.save(processingSchedule);
    verify(processingScheduleRepository).save(processingSchedule);
  }

  @Test
  public void shouldThrowErrorWhenTryingToSaveAScheduleWithNoName() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    doThrow(new RuntimeException("Schedule can not be saved without its name.")).when(processingScheduleRepository).save(processingSchedule);

    exException.expect(RuntimeException.class);
    exException.expectMessage("Schedule can not be saved without its name.");

    service.save(processingSchedule);
    verify(processingScheduleRepository).save(processingSchedule);
  }
}
