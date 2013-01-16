package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.web.controller.RnrController.RNR;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.when;

public class RnrControllerTest {

  MockHttpServletRequest request;
  private static final String USER = "user";
  private static final Integer USER_ID = 1;

  RnrService rnrService;

  RnrController controller;
  private Rnr rnr;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    rnrService = mock(RnrService.class);
    controller = new RnrController(rnrService);
    rnr = new Rnr();
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {

    controller.saveRnr(rnr, rnr.getId(), request);

    verify(rnrService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER_ID)));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(rnrService).initRnr(1, 2, null, USER_ID);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldReturnNullIfGettingRequisitionFails() throws Exception {
    Rnr expectedRnr = null;
    when(rnrService.get(1, 2)).thenReturn(expectedRnr);
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2);
    assertThat((Rnr) response.getBody().getData().get(RNR), is(expectedRnr));
  }

  @Test
  public void shouldAllowSubmittingOfRnrAndTagWithModifiedBy() throws Exception {
    when(rnrService.submit(rnr)).thenReturn(new OpenLmisMessage("test.msg.key"));
    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    assertThat(response.getBody().getSuccessMsg(), is("test.msg.key"));
    verify(rnrService).submit(rnr);
    assertThat(rnr.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldReturnErrorMessageIfRnrNotValidButShouldSaveIt() throws Exception {
    doThrow(new DataException(new OpenLmisMessage("some error"))).when(rnrService).submit(rnr);

    ResponseEntity<OpenLmisResponse> response = controller.submit(rnr, rnr.getId(), request);
    verify(rnrService).save(rnr);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldGiveMessageAndAuthorizeRnr() throws Exception {
    String code = RnrService.RNR_AUTHORIZED_SUCCESSFULLY;
    String message = "R&R authorized successfully!";

    when(rnrService.authorize(rnr)).thenReturn(new OpenLmisMessage(code));

    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    verify(rnrService).authorize(rnr);
    assertThat(response.getBody().getSuccessMsg(), is(message));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldNotAuthorizeRnrAndGiveErrorMessage() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(rnrService).authorize(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.authorize(rnr, rnr.getId(), request);

    verify(rnrService).save(rnr);
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveErrorResponseIfThereIsAnyExceptionWhileSavingRnr() throws Exception {
    String errorMessage = "some error";
    doThrow(new DataException(new OpenLmisMessage(errorMessage))).when(rnrService).save(rnr);
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);

    verify(rnrService).save(rnr);
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldGiveSuccessResponseIfRnrSavedSuccessfully() throws Exception {
    ResponseEntity<OpenLmisResponse> response = controller.saveRnr(rnr, rnr.getId(), request);
    verify(rnrService).save(rnr);
    assertThat(response.getBody().getSuccessMsg(), is("R&R saved successfully!"));
  }
}

