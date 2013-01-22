package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductPrice;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProgramProductRepository;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramProductBuilder.defaultProgramProduct;

@RunWith(MockitoJUnitRunner.class)
public class ProgramProductServiceTest {
  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Mock
  private ProgramProductRepository programProductRepository;

  private ProgramProductService programProductService;

  @Before
  public void setUp() throws Exception {
    programProductService = new ProgramProductService(programProductRepository);
  }

  @Test
  public void shouldUpdateCurrentPriceOfProgramProductCodeCombinationAndUpdatePriceHistory() throws Exception {
    ProgramProduct programProduct = make(a(defaultProgramProduct));
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
    programProductPrice.setModifiedBy("User");

    ProgramProduct returnedProgramProduct = new ProgramProduct();
    returnedProgramProduct.setId(123);
    when(programProductRepository.getProgramProductByProgramAndProductCode(programProduct)).thenReturn(returnedProgramProduct);

    programProductService.save(programProductPrice);

    assertThat(programProductPrice.getProgramProduct().getId(), is(123));
    assertThat(programProductPrice.getProgramProduct().getModifiedBy(), is("User"));
    verify(programProductRepository).getProgramProductByProgramAndProductCode(programProduct);
    verify(programProductRepository).updateCurrentPrice(programProduct);
    verify(programProductRepository).updatePriceHistory(programProductPrice);
  }

  @Test
  public void shouldValidateProgramProductPriceBeforeSaving() throws Exception {
    expectException.expect(DataException.class);
    expectException.expectMessage("error-code");

    ProgramProductPrice programProductPrice = mock(ProgramProductPrice.class);
    doThrow(new DataException("error-code")).when(programProductPrice).validate();

    programProductService.save(programProductPrice);
  }
}
