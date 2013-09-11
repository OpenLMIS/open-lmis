package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.web.controller.FacilityProgramProductController.PROGRAM_PRODUCT_LIST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RunWith(MockitoJUnitRunner.class)
public class ProgramProductControllerTest {

  @Mock
  ProgramProductService service;

  @InjectMocks
  ProgramProductController controller;

  @Test
  public void shouldGetProgramProductsByProgram() throws Exception {
    List<ProgramProduct> expectedProgramProductList = new ArrayList<>();
    Program program = new Program(1l);
    when(service.getByProgram(program)).thenReturn(expectedProgramProductList);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getProgramProductsByProgram(1l);

    assertThat((List<ProgramProduct>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
    verify(service).getByProgram(program);
  }

  @Test
  public void shouldGetProgramProductsByProgramCodeAndFacilityTypeCode() throws Exception {
    List<ProgramProduct> expectedProgramProductList = new ArrayList<>();
    Program program = new Program(1l);
    program.setCode("P1");

    FacilityType facilityType = new FacilityType();
    facilityType.setCode("warehouse");

    ResponseEntity<OpenLmisResponse> responseEntity = controller.getProgramProductsBy(program.getCode(), facilityType.getCode());

    assertThat((List<ProgramProduct>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
    verify(service).getProgramProductsBy(program.getCode(), facilityType.getCode());
  }

  @Test
  public void shouldReturnErrorResponseUponDataException() throws Exception {
    String programCode = "P1";
    String facilityTypeCode = "warehouse";

    String expectedErrorMsg = "program.code.invalid";

    doThrow(new DataException(expectedErrorMsg)).when(service).getProgramProductsBy(programCode, facilityTypeCode);

    ResponseEntity<OpenLmisResponse> errorResponse = controller.getProgramProductsBy(programCode, facilityTypeCode);

    verify(service).getProgramProductsBy(programCode, facilityTypeCode);

    assertThat(errorResponse.getStatusCode(), is(BAD_REQUEST));
    assertThat(errorResponse.getBody().getErrorMsg(), is(expectedErrorMsg));
  }
}
