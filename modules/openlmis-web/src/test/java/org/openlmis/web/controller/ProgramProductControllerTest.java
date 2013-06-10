package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.ProgramProductController.PROGRAM_PRODUCT_LIST;

@RunWith(PowerMockRunner.class)
public class ProgramProductControllerTest {


  @Mock
  private ProgramProductService programProductService;

  @InjectMocks
  private ProgramProductController programProductController;

  @Test
  public void shouldGetProgramProductsByProgram() throws Exception {
    List<ProgramProduct> expectedProgramProductList = new ArrayList<>();
    Long programId = 1l;
    when(programProductService.getProgramProductsWithISAByProgram(programId)).thenReturn(expectedProgramProductList);
    ResponseEntity<OpenLmisResponse> responseEntity = programProductController.getProgramProductsByProgram(programId);
    assertThat((List<ProgramProduct>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
    verify(programProductService).getProgramProductsWithISAByProgram(programId);
  }

  @Test
  public void shouldSaveProgramProductISA(){
    ProgramProductISA programProductISA = new ProgramProductISA();
    programProductController.saveProgramProductISA(programProductISA);
    verify(programProductService).saveProgramProductISA(programProductISA);
  }
}
