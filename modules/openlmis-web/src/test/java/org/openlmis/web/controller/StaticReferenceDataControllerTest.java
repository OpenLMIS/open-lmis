package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.web.configurationReader.StaticReferenceDataReader;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.StaticReferenceDataController.CURRENCY;

public class StaticReferenceDataControllerTest {

  private StaticReferenceDataController staticReferenceDataController;

  @Mock
  StaticReferenceDataReader staticReferenceDataReader;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    staticReferenceDataController = new StaticReferenceDataController(staticReferenceDataReader);
  }

  @Test
  public void shouldGetCurrency() throws Exception {
    when(staticReferenceDataReader.getCurrency()).thenReturn("$");
    ResponseEntity<OpenLmisResponse> response = staticReferenceDataController.getCurrency();

    OpenLmisResponse openLmisResponse = response.getBody();
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((String) openLmisResponse.getData().get(CURRENCY), is("$"));
  }
}
