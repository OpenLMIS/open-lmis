package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.service.GeographicZoneService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class GeographicZoneControllerTest {

  @Mock
  private GeographicZoneService service;

  @InjectMocks
  private GeographicZoneController controller;
  @Test
  public void shouldGetGeographicZoneById(){
    GeographicZone geographicZone = new GeographicZone();
    Long geoZoneId = 1l;
    when(service.getById(geoZoneId)).thenReturn(geographicZone);

    ResponseEntity<OpenLmisResponse> response = controller.get(geoZoneId);

    assertThat((GeographicZone) response.getBody().getData().get("geoZone"), is(geographicZone));
    verify(service).getById(geoZoneId);
  }
}
