package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RnrControllerTest {

  MockHttpServletRequest request;
  private static final String USER = "user";

  RnrService rnrService;

  RnrController controller;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    request.setSession(session);

    rnrService = mock(RnrService.class);
    controller = new RnrController(rnrService);
  }

  @Test
  public void shouldSaveWIPRnr() throws Exception {
    Rnr rnr = new Rnr();
    int programId = 456;
    int facilityId = 123;

    controller.saveRnr(rnr, facilityId, programId, request);

    verify(rnrService).save(rnr);
    assertThat(rnr.getModifiedBy(), is(equalTo(USER)));
    assertThat(rnr.getFacilityId(), is(facilityId));
    assertThat(rnr.getProgramId(), is(programId));
  }

  @Test
  public void shouldGiveErrorIfInitiatingFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(rnrService).initRnr(1, 2, USER);
    ResponseEntity<OpenLmisResponse> response = controller.initiateRnr(1, 2, request);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }

  @Test
  public void shouldGiveErrorIfGettingRequisitionFails() throws Exception {
    String errorMessage = "error-message";
    doThrow(new DataException(errorMessage)).when(rnrService).get(1, 2);
    ResponseEntity<OpenLmisResponse> response = controller.get(1, 2);
    assertThat(response.getBody().getErrorMsg(), is(equalTo(errorMessage)));
  }
}
