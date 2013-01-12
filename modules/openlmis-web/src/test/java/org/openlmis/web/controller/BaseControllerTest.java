package org.openlmis.web.controller;

import org.junit.Test;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BaseControllerTest {
  @Test
  public void shouldResolveUnhandledException() throws Exception {
    BaseController baseController = new BaseController();
    final ResponseEntity<OpenLmisResponse> response = baseController.handleException(new Exception());
    final OpenLmisResponse body = response.getBody();
    assertThat(body.getErrorMsg(), is("Oops, something has gone wrong. Please try again later"));
  }
}
