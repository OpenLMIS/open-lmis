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
import org.openlmis.core.builder.ISABuilder;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotNull;
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
    programProduct.setProduct(new Product());
    programProduct.getProduct().setPackSize(7);
    ProgramProductISA programProductIsa = mock(ProgramProductISA.class);
    programProduct.setProgramProductIsa(programProductIsa);

    when(programProductIsa.calculate(420L)).thenReturn(108);
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, 3L, null);

    assertThat(facilityProgramProduct.calculateIsa(420L, 3), is(46));
  }



  @Test
  public void shouldReturnOverriddenIsaAmountIfOverriddenISANotNull() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();
    programProduct.setProduct(new Product());
    programProduct.getProduct().setPackSize(7);

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, 3L);
    assertNull(facilityProgramProduct.calculateIsa(420L, 5));

    facilityProgramProduct.setOverriddenIsa(ISABuilder.build());
    assertNotNull(facilityProgramProduct.calculateIsa(420L, 5));
  }


  @Test
  public void shouldSetIdealQuantityAsNullIfNoISADefined() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, 3L, null);

    assertThat(facilityProgramProduct.calculateIsa(420L, 5), is(nullValue()));

  }

  @Test
  public void shouldSetIdealQuantityAsNullIfNoPopulationDefined() throws Exception {
    ProgramProduct programProduct = new ProgramProduct();

    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct(programProduct, 3L, null);

    assertThat(facilityProgramProduct.calculateIsa(null, 5), is(nullValue()));

  }

  @Test
  public void shouldReturnWhoCode() throws Exception {
    String productCode = "BCG";
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();

    Product product = mock(Product.class);
    when(product.getCode()).thenReturn(productCode);
    facilityProgramProduct.setProduct(product);

    ProgramProductISA programProductISA = mock(ProgramProductISA.class);
    when(programProductISA.getWhoRatio()).thenReturn(1234D);

    facilityProgramProduct.setProgramProductIsa(programProductISA);

    assertThat(facilityProgramProduct.getWhoRatio(/*productCode*/), is(1234D));
  }

  @Test
  public void shouldReturnNullWhoRatioIfProgramProductIsaIsNull() throws Exception {
    String productCode = "BCG";
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();

    Product product = mock(Product.class);
    when(product.getCode()).thenReturn(productCode);
    facilityProgramProduct.setProduct(product);

    assertThat(facilityProgramProduct.getWhoRatio(), is(nullValue()));
  }

  /*
  @Test
  public void shouldReturnNullWhoRatioIfProductCodeInvalid() throws Exception {
    String productCode = "BCG";
    FacilityProgramProduct facilityProgramProduct = new FacilityProgramProduct();

    Product product = mock(Product.class);
    when(product.getCode()).thenReturn(productCode);
    facilityProgramProduct.setProduct(product);

    ProgramProductISA programProductISA = mock(ProgramProductISA.class);
    when(programProductISA.getWhoRatio()).thenReturn(1234D);

    facilityProgramProduct.setProgramProductIsa(programProductISA);

    assertThat(facilityProgramProduct.getWhoRatio("invalidProductCode"), is(nullValue()));
  } */


  @Test
  public void shouldReturnActiveProductsOnly() {
    Long facilityId = 1L;
    Product product = new Product();
    product.setActive(true);
    FacilityProgramProduct programProductActive = new FacilityProgramProduct(new ProgramProduct(new Program(), product, 10, true),
      facilityId, null);
    FacilityProgramProduct programProductInactive = new FacilityProgramProduct(new ProgramProduct(new Program(), product, 10, false)
      , facilityId, null);

    List<FacilityProgramProduct> activeFacilityProgramProducts = FacilityProgramProduct.filterActiveProducts(asList(programProductActive, programProductInactive));

    assertThat(activeFacilityProgramProducts.size(), is(1));
  }
}
