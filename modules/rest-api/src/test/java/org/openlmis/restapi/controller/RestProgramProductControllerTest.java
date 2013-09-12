package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.dto.ProgramProductDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.response.RestResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.controller.BaseController.UNEXPECTED_EXCEPTION;
import static org.openlmis.restapi.controller.RestProgramProductController.PROGRAM_PRODUCT_LIST;
import static org.openlmis.restapi.response.RestResponse.ERROR;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.springframework.http.HttpStatus.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RestResponse.class)
public class RestProgramProductControllerTest {

  @Mock
  ProgramProductService service;

  @Mock
  MessageService messageService;

  @InjectMocks
  RestProgramProductController controller;

  @Before
  public void setUp() throws Exception {
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldGetProgramProductsByProgramCodeAndFacilityTypeCode() throws Exception {
    List<ProgramProductDTO> expectedProgramProductList = new ArrayList<>();
    Program program = new Program(1l);
    program.setCode("P1");

    FacilityType facilityType = new FacilityType();
    facilityType.setCode("warehouse");

    when(service.getProgramProductsBy(program.getCode(), facilityType.getCode())).thenReturn(new ArrayList<ProgramProduct>());

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(PROGRAM_PRODUCT_LIST, expectedProgramProductList), OK);

    when(RestResponse.response(PROGRAM_PRODUCT_LIST, expectedProgramProductList)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> responseEntity = controller.getProgramProductsBy(program.getCode(), facilityType.getCode());

    assertThat((ArrayList<ProgramProductDTO>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
    verify(service).getProgramProductsBy(program.getCode(), facilityType.getCode());
  }

  @Test
  public void shouldReturnErrorResponseUponDataException() throws Exception {
    String programCode = "P1";
    String facilityTypeCode = "warehouse";

    String expectedErrorMsg = "program.code.invalid";

    DataException dataException = new DataException(expectedErrorMsg);
    doThrow(dataException).when(service).getProgramProductsBy(programCode, facilityTypeCode);
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, expectedErrorMsg), BAD_REQUEST);
    when(RestResponse.error(dataException.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> errorResponse = controller.getProgramProductsBy(programCode, facilityTypeCode);

    verify(service).getProgramProductsBy(programCode, facilityTypeCode);

    assertThat(errorResponse, is(expectedResponse));
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    String errorMessage = "Oops, something has gone wrong. Please try again later";
    when(messageService.message(UNEXPECTED_EXCEPTION)).thenReturn(errorMessage);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), INTERNAL_SERVER_ERROR);

    when(RestResponse.error(UNEXPECTED_EXCEPTION, INTERNAL_SERVER_ERROR)).thenReturn(expectedResponse);

    final ResponseEntity<RestResponse> response = controller.handleException(new Exception());

    final RestResponse body = response.getBody();
    assertThat((String) body.getData().get(ERROR), is(errorMessage));
  }

}
