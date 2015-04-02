package org.openlmis.web.controller.vaccine;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.DiscardingReason;
import org.openlmis.vaccine.service.DiscardingReasonsService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.http.ResponseEntity;

import java.util.List;


@Category(UnitTests.class)
@PrepareForTest(DiscardingReasonsController.class)
public class DiscardingReasonsControllerTest {

  @Mock
  DiscardingReasonsService service;

  @InjectMocks
  DiscardingReasonsController controller;

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Test
  public void shouldGetAll() throws Exception {
    controller.getAll();
    verify(service).getAllReasons();
  }

  @Test
  public void shouldGetDataFromGetAll(){
    List<DiscardingReason> reasons = asList(new DiscardingReason());
    when(service.getAllReasons()).thenReturn(reasons);
    ResponseEntity<OpenLmisResponse> response = controller.getAll();
    assertThat(reasons, is(response.getBody().getData().get("reasons")));
  }
}