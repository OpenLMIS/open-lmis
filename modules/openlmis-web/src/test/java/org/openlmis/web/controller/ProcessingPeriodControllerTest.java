package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProcessingPeriodControllerTest {

  @Rule
  public ExpectedException exException = none();

  private ProcessingPeriodController controller;

  @Mock
  private ProcessingScheduleService service;
  private final Integer SCHEDULE_ID = 123;
  private final Integer USER_ID = 5;
  private MockHttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    controller = new ProcessingPeriodController(service);

    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);
    request.setSession(session);
  }

  @Test
  public void shouldGetAllPeriodsForGivenSchedule() throws Exception {
    List<ProcessingPeriod> mockedList = new ArrayList<>();
    when(service.getAllPeriods(SCHEDULE_ID)).thenReturn(mockedList);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getAll(SCHEDULE_ID);

    List<ProcessingPeriod> periodList = (List<ProcessingPeriod>) responseEntity.getBody().getData().get(ProcessingPeriodController.PERIODS);
    verify(service).getAllPeriods(SCHEDULE_ID);
    assertThat(periodList, is(mockedList));
  }

  @Test
  public void shouldSaveAPeriodForGivenSchedule() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();

    ResponseEntity<OpenLmisResponse> responseEntity = controller.save(SCHEDULE_ID, processingPeriod, request);

    verify(service).savePeriod(processingPeriod);
    assertThat(processingPeriod.getScheduleId(), is(SCHEDULE_ID));
    assertThat(processingPeriod.getModifiedBy(), is(USER_ID));
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody().getSuccessMsg(), is("Period added successfully"));
  }

  @Test
  public void shouldGiveErrorResponseWhenCreatingANewPeriodWithExistingNameForAGivenSchedule() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    doThrow(DataException.class).when(service).savePeriod(processingPeriod);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.save(SCHEDULE_ID, processingPeriod, request);

    verify(service).savePeriod(processingPeriod);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
    assertThat(responseEntity.getBody().getErrorMsg(), is("Period Name already exists for this schedule"));
  }

  @Test
  public void shouldGiveErrorResponseWhenCreatingANewPeriodWhenProcessingPeriodIsNotValid() throws Exception {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    doThrow(new RuntimeException("errorMsg")).when(service).savePeriod(processingPeriod);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.save(SCHEDULE_ID, processingPeriod, request);

    verify(service).savePeriod(processingPeriod);
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(responseEntity.getBody().getErrorMsg(), is("errorMsg"));
  }

}
