/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;


import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.ProductBuilder.active;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProductMapperIT {

  public static final String PRODUCT_DOSAGE_UNIT_MG = "mg";
  private static final String PRODUCT_FORM_TABLET = "Tablet";

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  @Autowired
  ProgramProductMapper programProductMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  ProgramSupportedMapper programSupportedMapper;
  @Autowired
  ProductGroupMapper productGroupMapper;

  @Autowired
  ProductMapper productMapper;

  @Autowired
  ProductCategoryMapper productCategoryMapper;

  @Test
  public void shouldNotSaveProductWithoutMandatoryFields() throws Exception {
    expectedEx.expect(DataIntegrityViolationException.class);
    expectedEx.expectMessage("null value in column \"primaryname\" violates not-null constraint");
    Product product = new Product();
    product.setCode("ABCD123");
    Long status = productMapper.insert(product);
    Long expected = 0L;
    assertEquals( expected , status);
  }

  @Test
  public void shouldReturnDosageUnitIdForCode() {
    Long id = productMapper.getDosageUnitIdForCode(PRODUCT_DOSAGE_UNIT_MG);
    assertThat(id, CoreMatchers.is(1L));

    id = productMapper.getDosageUnitIdForCode("invalid dosage unit");
    assertThat(id, CoreMatchers.is(nullValue()));
  }

  @Test
  public void shouldReturnProductFormIdForCode() {
    Long id = productMapper.getProductFormIdForCode(PRODUCT_FORM_TABLET);
    assertThat(id, CoreMatchers.is(1L));

    id = productMapper.getProductFormIdForCode("invalid product form");
    assertThat(id, CoreMatchers.is(nullValue()));
  }

  @Test
  public void shouldReturnNullForInvalidProductCode() {
    String code = "invalid_code";
    Long productId = productMapper.getIdByCode(code);
    assertThat(productId, is(nullValue()));
  }

  @Test
  public void shouldReturnProductIdForValidProductCode() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);
    Long id = productMapper.getIdByCode(product.getCode());
    assertThat(id, is(product.getId()));
  }

  @Test
  public void shouldReturnProductByCode() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);
    Product expectedProduct = productMapper.getByCode(product.getCode());
    assertThat(expectedProduct.getId(), is(product.getId()));
    assertThat(expectedProduct.getCode(), is(product.getCode()));
    assertThat(expectedProduct.getPrimaryName(), is(product.getPrimaryName()));
    assertThat(expectedProduct.getForm().getCode(), is("Tablet"));
    assertThat(expectedProduct.getDosageUnit().getCode(), is("mg"));
  }


  @Test
  public void shouldUpdateProduct() {
    Product product = make(a(defaultProduct));
    productMapper.insert(product);

    product.setPrimaryName("Updated Name");
    product.setAlternateItemCode("Alternate Code");

    productMapper.update(product);

    Product returnedProduct = productMapper.getByCode(product.getCode());

    assertThat(returnedProduct.getPrimaryName(), is("Updated Name"));
    assertThat(returnedProduct.getAlternateItemCode(), is("Alternate Code"));

  }

  @Test
  public void shouldGetAProductById() {
    Product product = make(a(defaultProduct));

    productMapper.insert(product);

    Product returnedProduct = productMapper.getById(product.getId());

    assertThat(returnedProduct.getId(), is(product.getId()));
    assertThat(returnedProduct.getCode(), is(product.getCode()));
    assertThat(returnedProduct.getProductGroup(), is(product.getProductGroup()));
    assertThat(returnedProduct.getPrimaryName(), is(product.getPrimaryName()));
  }

  @Test
  public void shouldReturnFalseIfProductInactive() throws Exception {
    Product product = make(a(defaultProduct, with(active, false)));

    productMapper.insert(product);

    assertFalse(productMapper.isActive(product.getCode()));
  }

  @Test
  public void shouldReturnTrueIfProductActive() throws Exception {
    Product product = make(a(defaultProduct, with(active, true)));

    productMapper.insert(product);

    assertTrue(productMapper.isActive(product.getCode()));
  }
}
