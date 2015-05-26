package org.openlmis.web.controller.vaccine;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.VaccineProductDose;
import org.openlmis.vaccine.dto.ProductDoseDTO;
import org.openlmis.vaccine.dto.VaccineServiceConfigDTO;
import org.openlmis.vaccine.service.VaccineProductDoseService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductDoseControllerTest {

  @Mock
  VaccineProductDoseService service;

  @InjectMocks
  ProductDoseController controller;

  @Test
  public void shouldGetProgramProtocol() throws Exception {
    VaccineServiceConfigDTO dto = new VaccineServiceConfigDTO();
    when(service.getProductDoseForProgram(1L)).thenReturn(dto);
    ResponseEntity<OpenLmisResponse> response = controller.getProgramProtocol(1L);
    assertThat(response.getBody().getData().get("protocol"), is(notNullValue()));
  }

  @Test
  public void shouldSave() throws Exception {
    VaccineServiceConfigDTO dto = new VaccineServiceConfigDTO();
    dto.setProtocols(new ArrayList<ProductDoseDTO>());
    doNothing().when(service).save(dto.getProtocols());

    ResponseEntity<OpenLmisResponse> response = controller.save(dto);

    verify(service).save(anyList());
    assertThat(dto, is(response.getBody().getData().get("protocol")));
  }
}