package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.web.controller.RequisitionController.*;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

public class RequisitionControllerTest {

  MockHttpServletRequest request;
  private static final String USER = "user";
  private static final Integer USER_ID = 1;

  RequisitionService requisitionService;

  RequisitionController controller;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    requisitionService = mock(RequisitionService.class);
    controller = new RequisitionController(requisitionService);
    rnr = new Rnr();
  }

  @Test
  public void shouldInitiateRnr() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, 3, request);

    verify(requisitionService).initiate(1, 2, 3, USER_ID);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldGetRnrIfExists() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2, 3);

    verify(requisitionService).get(1, 2, 3);
    assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {
    controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(requisitionService).initiate(1, 2, null, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, null, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldReturnNullIfGettingRequisitionFails() throws Exception {
    Rnr expectedRnr = null;
    when(requisitionService.get(1, 2, null)).thenReturn(expectedRnr);
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2, null);
    assertThat((Rnr) response.getBody().getData().get(RNR), is(expectedRnr));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    when(requisitionService.submit(rnr)).thenReturn(new OpenLmisMessage("test.msg.key"));
    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getBody().getSuccessMsg(), is("test.msg.key"));
    verify(requisitionService).submit(rnr);
    assertThat(rnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldReturnErrorMessageIfRnrNotValid() throws Exception {
    doThrow(new DataException(new OpenLmisMessage("some error"))).when(requisitionService).submit(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldGiveMessageAndAuthorizeRnr() throws Exception {
    String code = RequisitionService.RNR_AUTHORIZED_SUCCESSFULLY;
    String message = "R&R authorized successfully!";

    when(requisitionService.authorize(rnr)).thenReturn(new OpenLmisMessage(code));

    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    verify(requisitionService).authorize(rnr);
    assertThat(response.getBody().getSuccessMsg(), is(message));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldNotAuthorizeRnrAndGiveErrorMessage() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(requisitionService).authorize(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveErrorResponseIfThereIsAnyExceptionWhileSavingRnr() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(requisitionService).save(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);

    verify(requisitionService).save(rnr);
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveSuccessResponseIfRnrSavedSuccessfully() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);
    verify(requisitionService).save(rnr);
    assertThat(response.getBody().getSuccessMsg(), is("R&R saved successfully!"));
  }

  @Test
  public void shouldReturnListOfUserSupervisedRnrForApproval() {
    List<RnrDTO> rnrList = new ArrayList<>();
    ResponseEntity<OpenLmisResponse> response = controller.fetchUserSupervisedRnrForApproval(request);
    verify(requisitionService).listForApproval(USER_ID);
    assertThat((List<RnrDTO>) response.getBody().getData().get(RNR_LIST), is(rnrList));
  }

  @Test
  public void shouldReturnAllPeriodsForInitiatingRequisition() {
    ProcessingPeriod processingPeriod = new ProcessingPeriod();
    processingPeriod.setId(6);
    List<ProcessingPeriod> periodList = Arrays.asList(processingPeriod);
    Rnr rnr = new Rnr();

    when(requisitionService.getAllPeriodsForInitiatingRequisition(1, 2)).thenReturn(periodList);
    when(requisitionService.get(1, 2, 6)).thenReturn(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(1, 2);

    verify(requisitionService).getAllPeriodsForInitiatingRequisition(1, 2);
    assertThat((List<ProcessingPeriod>) response.getBody().getData().get(PERIODS), is(periodList));
    assertThat((Rnr) response.getBody().getData().get(RNR), is(rnr));
  }

  @Test
  public void shouldReturnErrorResponseIfNoPeriodsFoundForInitiatingRequisition() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(requisitionService).getAllPeriodsForInitiatingRequisition(1, 2);

    ResponseEntity<OpenLmisResponse> response = controller.getAllPeriodsForInitiatingRequisitionWithRequisitionStatus(1, 2);

    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }
}

