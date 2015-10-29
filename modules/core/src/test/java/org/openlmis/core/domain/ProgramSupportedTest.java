/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
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
  public void shouldReturnWhoRatio() {
    Double whoRatio = 127D;
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();

    FacilityProgramProduct facilityProgramProduct = mock(FacilityProgramProduct.class);
    when(facilityProgramProduct.getWhoRatio(/*productCode*/)).thenReturn(whoRatio);

    Product product = mock(Product.class);
    when(product.getCode()).thenReturn(productCode);
    when(facilityProgramProduct.getProduct()).thenReturn(product);

    programSupported.setProgramProducts(asList(facilityProgramProduct));

    assertThat(programSupported.getWhoRatioFor(productCode), is(whoRatio));
  }

  @Test
  public void shouldReturnNullIfNoProgramProducts() {
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setProgramProducts(Collections.<FacilityProgramProduct>emptyList());

    assertThat(programSupported.getWhoRatioFor(productCode), is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfInvalidProductCode() {
    Double whoRatio = 127D;
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();
    FacilityProgramProduct facilityProgramProduct = mock(FacilityProgramProduct.class);
    when(facilityProgramProduct.getWhoRatio()).thenReturn(whoRatio);
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

  @Test
  public void shouldReturnPackSizeForProductIfItExistsInFPPs() {
    Integer packSize = 1000;
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();

    FacilityProgramProduct facilityProgramProduct = mock(FacilityProgramProduct.class);
    Product product = mock(Product.class);

    when(facilityProgramProduct.getProduct()).thenReturn(product);
    when(product.getCode()).thenReturn(productCode);
    when(product.getPackSize()).thenReturn(packSize);

    programSupported.setProgramProducts(asList(facilityProgramProduct));

    assertThat(programSupported.getPackSizeFor(productCode), is(packSize));
  }

  @Test
  public void shouldReturnPackSizeNullIfNoProductExistsWithProductCode() {
    String productCode = "BCG";
    ProgramSupported programSupported = new ProgramSupported();
    programSupported.setProgramProducts(Collections.<FacilityProgramProduct>emptyList());

    assertThat(programSupported.getPackSizeFor(productCode), is(nullValue()));
  }

}
