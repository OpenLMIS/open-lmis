package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.builder.ProgramProductBuilder;
import org.openlmis.core.exception.DataException;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.openlmis.core.domain.ProgramProduct.PROGRAM_PRODUCT_INVALID_CURRENT_PRICE;

public class ProgramProductTest {
  @Rule
  public ExpectedException expectException = ExpectedException.none();

  @Test
  public void shouldThrowExceptionIfPricePerDosageIsNegativeOnValidation() throws Exception {
    ProgramProduct programProduct = make(a(ProgramProductBuilder.defaultProgramProduct));
    programProduct.setCurrentPrice(new Money("-0.01"));
    expectException.expect(DataException.class);
    expectException.expectMessage(PROGRAM_PRODUCT_INVALID_CURRENT_PRICE);
    programProduct.validate();
  }
}
