package org.openlmis.distribution.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.distribution.domain.AllocationProgramProduct;
import org.openlmis.distribution.response.AllocationResponse;
import org.openlmis.distribution.service.AllocationProgramProductService;
import org.openlmis.distribution.domain.ProgramProductISA;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.distribution.controller.ProgramProductController.PROGRAM_PRODUCT_LIST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class ProgramProductControllerTest {


  @Mock
  private AllocationProgramProductService programProductISAService;

  @InjectMocks
  private ProgramProductController programProductController;

  @Test
  public void shouldGetProgramProductsByProgram() throws Exception {
    List<AllocationProgramProduct> expectedProgramProductList = new ArrayList<>();
    Long programId = 1l;
    when(programProductISAService.getProgramProductsWithISAByProgram(programId)).thenReturn(expectedProgramProductList);
    ResponseEntity<AllocationResponse> responseEntity = programProductController.getProgramProductsByProgram(programId);
    assertThat((List<AllocationProgramProduct>) responseEntity.getBody().getData().get(PROGRAM_PRODUCT_LIST), is(expectedProgramProductList));
    verify(programProductISAService).getProgramProductsWithISAByProgram(programId);
  }

  @Test
  public void shouldSaveProgramProductISA(){
    ProgramProductISA programProductISA = new ProgramProductISA();
    Long programProductId = 1l;
    programProductController.saveProgramProductISA(programProductId, programProductISA);
    verify(programProductISAService).saveProgramProductISA(programProductId, programProductISA);
  }
}
