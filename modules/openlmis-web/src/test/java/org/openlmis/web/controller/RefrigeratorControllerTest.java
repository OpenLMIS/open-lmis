package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RefrigeratorControllerTest {

  @Mock
  RefrigeratorService refrigeratorService;

  @InjectMocks
  RefrigeratorController controller;

  @Test
  public void shouldGetRefrigeratorsForADeliveryZoneAndProgram() throws Exception {

    Long deliveryZoneId = 1L;
    Long programId = 1L;

    List<Refrigerator> refrigerators = new ArrayList<>();
    when(refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId)).thenReturn(refrigerators);

    ResponseEntity<OpenLmisResponse> response = controller.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);

    List<Refrigerator> result = (List<Refrigerator>) response.getBody().getData().get(RefrigeratorController.REFRIGERATORS);
    assertThat(result, is(refrigerators));
  }
}
