package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.ProcessingScheduleController.SCHEDULE;
import static org.openlmis.web.controller.ProcessingScheduleController.SCHEDULES;

public class ProcessingScheduleControllerTest {
  @Rule
  public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();

  @Mock
  ProcessingScheduleService processingScheduleService;

  ProcessingScheduleController processingScheduleController;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    processingScheduleController = new ProcessingScheduleController(processingScheduleService);
  }

  @Test
  public void shouldGetAll() throws Exception {
    List<ProcessingSchedule> processingSchedules = new ArrayList<>();
    when(processingScheduleService.getAll()).thenReturn(processingSchedules);

    ResponseEntity<OpenLmisResponse> responseEntity = processingScheduleController.getAll();

    Map<String, Object> responseEntityData = responseEntity.getBody().getData();
    assertThat((List<ProcessingSchedule>) responseEntityData.get(SCHEDULES), is(processingSchedules));
  }

  @Test
  public void shouldCreateAndReturnANewSchedule() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleService.save(processingSchedule)).thenReturn(mockedSchedule);

    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule);

    assertThat(response, is(notNullValue()));
    ProcessingSchedule savedSchedule = (ProcessingSchedule) response.getBody().getData().get(SCHEDULE);
    assertThat(savedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldReturnErrorResponseWhenTryingToSaveAScheduleWithNoCodeSet() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    doThrow(new RuntimeException("Schedule can not be saved without its code.")).when(processingScheduleService).save(processingSchedule);
    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule);

    verify(processingScheduleService).save(processingSchedule);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Schedule can not be saved without its code."));
  }

  @Test
  public void shouldReturnErrorResponseWhenTryingToSaveAScheduleWithNoNameSet() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    doThrow(new RuntimeException("Schedule can not be saved without its name.")).when(processingScheduleService).save(processingSchedule);
    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule);

    verify(processingScheduleService).save(processingSchedule);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Schedule can not be saved without its name."));
  }
}
