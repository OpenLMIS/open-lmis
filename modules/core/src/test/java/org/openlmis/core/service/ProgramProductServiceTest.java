package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.repository.ProgramProductRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProgramProductServiceTest {
  @Mock
  private ProgramProductRepository programProductRepository;

  ProgramProductService programProductService;

  @Test
  public void shouldUpdateCurrentPriceOfProgramProductAndUpdateCostHistory() throws Exception {
    programProductService = new ProgramProductService(programProductRepository);
    ProgramProductPrice programProductPrice = new ProgramProductPrice();
    ProgramProduct programProduct = new ProgramProduct();
    programProductPrice.setProgramProduct(programProduct);
    programProductService.save(programProductPrice);
    verify(programProductRepository).updateCurrentPrice(programProduct);
    verify(programProductRepository).updatePriceHistory(programProductPrice);
  }
}
