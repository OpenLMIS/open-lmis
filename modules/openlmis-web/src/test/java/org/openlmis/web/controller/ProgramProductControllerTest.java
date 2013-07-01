package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.AllocationProgramProductController.PROGRAM_PRODUCT_LIST;

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
}
