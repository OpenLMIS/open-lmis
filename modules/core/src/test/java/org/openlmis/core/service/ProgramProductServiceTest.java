package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.repository.ProgramProductRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramProductServiceTest {
  @Mock
  private ProgramProductRepository programProductRepository;

  ProgramProductService programProductService;

  @Test
  public void shouldUpdateCurrentPriceOfProgramProductCodeCombinationAndUpdatePriceHistory() throws Exception {
    programProductService = new ProgramProductService(programProductRepository);
    ProgramProductPrice programProductPrice = new ProgramProductPrice();
    ProgramProduct programProduct = new ProgramProduct();
    programProductPrice.setProgramProduct(programProduct);
    ProgramProduct returnedProgramProduct = new ProgramProduct();
    returnedProgramProduct.setId(123);
    when(programProductRepository.getProgramProductByProgramAndProductCode(programProduct)).thenReturn(returnedProgramProduct);
    programProductService.save(programProductPrice);

    assertThat(programProductPrice.getProgramProduct().getId(), is(123));
    verify(programProductRepository).getProgramProductByProgramAndProductCode(returnedProgramProduct);
    verify(programProductRepository).updateCurrentPrice(returnedProgramProduct);
    verify(programProductRepository).updatePriceHistory(programProductPrice);
  }
}
