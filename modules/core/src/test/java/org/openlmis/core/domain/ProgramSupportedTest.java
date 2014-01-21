package org.openlmis.core.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class ProgramSupportedTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldReturnWhoRatio() throws Exception {
    Double whoRatio = 127D;
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();

    FacilityProgramProduct facilityProgramProduct = mock(FacilityProgramProduct.class);
    when(facilityProgramProduct.getWhoRatio(productCode)).thenReturn(whoRatio);

    Product product = mock(Product.class);
    when(product.getCode()).thenReturn(productCode);
    when(facilityProgramProduct.getProduct()).thenReturn(product);

    programSupported.setProgramProducts(asList(facilityProgramProduct));

    assertThat(programSupported.getWhoRatioFor(productCode), is(whoRatio));
  }

  @Test
  public void shouldReturnNullIfNoProgramProducts() throws Exception {
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setProgramProducts(Collections.<FacilityProgramProduct>emptyList());

    assertThat(programSupported.getWhoRatioFor(productCode), is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfInvalidProductCode() throws Exception {
    Double whoRatio = 127D;
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();
    FacilityProgramProduct facilityProgramProduct = mock(FacilityProgramProduct.class);
    when(facilityProgramProduct.getWhoRatio(productCode)).thenReturn(whoRatio);
    Product product = mock(Product.class);
    when(product.getCode()).thenReturn(productCode);
    when(facilityProgramProduct.getProduct()).thenReturn(product);
    programSupported.setProgramProducts(asList(facilityProgramProduct));

    assertThat(programSupported.getWhoRatioFor("invalidProductCode"), is(nullValue()));
  }

  @Test
  public void shouldThrowExceptionIfActiveAndStartDateNull() throws Exception {
    ProgramSupported programSupported = new ProgramSupported(12345L, true, null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("supported.programs.invalid");

    programSupported.isValid();
  }
}
