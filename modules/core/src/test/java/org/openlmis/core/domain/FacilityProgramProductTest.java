/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.domain;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class FacilityProgramProductTest {
  @Test
  public void shouldReturnProductGroupIfProgramProductActiveAndProductActive() throws Exception {
    ProductGroup productGroup = new ProductGroup();

    Product product = new Product();
    product.setActive(true);
    product.setProductGroup(productGroup);

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();
    facilityProgramProduct.setActive(true);
    facilityProgramProduct.setProduct(product);

    assertThat(facilityProgramProduct.getActiveProductGroup(), is(productGroup));
  }

  @Test
  public void shouldReturnNullIfProgramProductInActiveAndProductActive() throws Exception {
    ProductGroup productGroup = new ProductGroup();

    Product product = new Product();
    product.setActive(true);
    product.setProductGroup(productGroup);

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();
    facilityProgramProduct.setActive(false);
    facilityProgramProduct.setProduct(product);

    assertNull(facilityProgramProduct.getActiveProductGroup());
  }

  @Test
  public void shouldReturnNullIfProgramProductActiveAndProductInActive() throws Exception {
    ProductGroup productGroup = new ProductGroup();

    Product product = new Product();
    product.setActive(false);
    product.setProductGroup(productGroup);

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();
    facilityProgramProduct.setActive(true);
    facilityProgramProduct.setProduct(product);

    assertNull(facilityProgramProduct.getActiveProductGroup());
  }

  @Test
  public void shouldReturnNullIfProgramProductInActiveAndProductInActive() throws Exception {
    ProductGroup productGroup = new ProductGroup();

    Product product = new Product();
    product.setActive(false);
    product.setProductGroup(productGroup);

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();
    facilityProgramProduct.setActive(false);
    facilityProgramProduct.setProduct(product);

    assertNull(facilityProgramProduct.getActiveProductGroup());
  }

  @Test
  public void shouldCalculateIsaIfOverriddenISAIsNull() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    ProgramProductISA programProductIsa = mock(ProgramProductISA.class);
    programProduct.setProgramProductIsa(programProductIsa);

    when(programProductIsa.calculate(420L)).thenReturn(108);
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, 3L, null);

    assertThat(facilityProgramProduct.calculateIsa(420L), is(108));
  }

  @Test
  public void shouldReturnOverriddenIsaIfOverriddenISANotNull() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, 3L, 98);

    assertThat(facilityProgramProduct.calculateIsa(420L), is(98));
  }
}
