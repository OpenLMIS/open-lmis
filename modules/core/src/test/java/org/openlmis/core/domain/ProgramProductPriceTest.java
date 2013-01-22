package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.domain.ProgramProductPrice.PROGRAM_PRODUCT_PRICE_INVALID_PRICE_PER_DOSAGE;

public class ProgramProductPriceTest {

  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfPricePerDosageIsNegativeOnValidation() throws Exception {
    ProgramProduct programProduct = mock(ProgramProduct.class);
    doNothing().when(programProduct).validate();
    ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("-1"), "source");
    expectException.expect(DataException.class);
    expectException.expectMessage(PROGRAM_PRODUCT_PRICE_INVALID_PRICE_PER_DOSAGE);
    programProductPrice.validate();
  }

  @Test
    public void shouldValidateProgramProductWhenValidatingProgramProductPrice() throws Exception {
      ProgramProduct programProduct = mock(ProgramProduct.class);
      ProgramProductPrice programProductPrice = new ProgramProductPrice(programProduct, new Money("1"), "source");
      programProductPrice.validate();
      verify(programProduct).validate();
  }
}
