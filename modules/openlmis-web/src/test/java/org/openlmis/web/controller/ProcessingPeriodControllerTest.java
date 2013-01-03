package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingPeriodControllerTest {

  private ProcessingPeriodController controller;

  @Mock
  @SuppressWarnings("unused")
  private ProcessingScheduleService service;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    controller = new ProcessingPeriodController(service);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    ResponseEntity<OpenLmisResponse> responseEntity = controller.getAll(123);
    List<ProcessingPeriod> mockedList = new ArrayList<>();
    when(service.getAllPeriods(123)).thenReturn(mockedList);

    List<ProcessingPeriod> periodList = (List<ProcessingPeriod>) responseEntity.getBody().getData().get(ProcessingPeriodController.PERIODS);
    verify(service).getAllPeriods(123);
    assertThat(periodList, is(mockedList));
  }
}
