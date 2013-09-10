/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;


import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProductBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.*;
import static org.apache.commons.collections.CollectionUtils.exists;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProductBuilder.code;
import static org.openlmis.core.builder.ProductBuilder.displayOrder;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ProgramProductMapperIT {
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  ProgramProductMapper programProductMapper;
  private Product product;
  private Program program;
  @Autowired
  private ProgramProductIsaMapper programProductISAMapper;
  @Autowired
  private FacilityApprovedProductMapper facilityApprovedProductMapper;
  @Autowired
  private ProductCategoryMapper productCategoryMapper;

  @Before
  public void setup() {
    product = make(a(ProductBuilder.defaultProduct, with(displayOrder, 1)));

    ProductCategory productCategory = new ProductCategory("10", "P1", 1);
    productCategoryMapper.insert(productCategory);
    product.setCategory(productCategory);

    productMapper.insert(product);
    program = make(a(defaultProgram));
    programMapper.insert(program);
  }

  @Test
  public void shouldInsertProductForAProgram() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    assertEquals(1, programProductMapper.insert(programProduct).intValue());
    assertNotNull(programProduct.getId());
  }

  @Test
  public void shouldGetProgramProductIdByProgramIdAndProductId() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    Long id = programProductMapper.getIdByProgramAndProductId(program.getId(), product.getId());

    assertThat(id, is(programProduct.getId()));
  }

  @Test
  public void shouldGetProgramProductByProgramIdAndProductId() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProduct result = programProductMapper.getByProgramAndProductId(program.getId(), product.getId());

    assertThat(result.getId(), is(programProduct.getId()));
  }

  @Test
  public void shouldUpdateCurrentPriceForProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true, new Money("100.0"));
    programProduct.setModifiedBy(1L);
    programProduct.setModifiedDate(new Date());
    programProductMapper.insert(programProduct);
    Money price = new Money("200.01");
    programProduct.setCurrentPrice(price);

    programProductMapper.updateCurrentPrice(programProduct);

    ProgramProduct returnedProgramProduct = programProductMapper.getByProgramAndProductId(program.getId(), product.getId());
    assertThat(returnedProgramProduct.getCurrentPrice(), is(price));
    assertThat(returnedProgramProduct.getModifiedBy(), is(1L));
    assertThat(returnedProgramProduct.getModifiedDate(), is(notNullValue()));
  }

  @Test
  public void shouldUpdateProgramProduct() throws Exception {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);

    programProductMapper.insert(programProduct);
    programProduct.setDosesPerMonth(10);
    programProduct.setActive(false);

    programProductMapper.update(programProduct);

    ProgramProduct dbProgramProduct = programProductMapper.getByProgramAndProductId(program.getId(), product.getId());

    assertThat(dbProgramProduct.getDosesPerMonth(), is(10));
    assertThat(dbProgramProduct.isActive(), is(false));
  }

  @Test
  public void shouldGetProgramProductsByProgram() {
    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    programProductMapper.insert(programProduct);

    ProgramProductISA programProductISA = new ProgramProductISA(programProduct.getId(), 1d, 2, 3.3, 5.6, 4, 5, 5);

    programProductISAMapper.insert(programProductISA);

    List<ProgramProduct> programProducts = programProductMapper.getByProgram(program);

    assertThat(programProducts.size(), is(1));
    assertThat(programProducts.get(0).getId(), is(programProduct.getId()));
    assertThat(programProducts.get(0).getProgramProductIsa(), is(programProductISA));
  }

  @Test
  public void shouldGetById() throws Exception {

    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);

    programProductMapper.insert(programProduct);

    ProgramProduct savedProgramProduct = programProductMapper.getById(programProduct.getId());

    assertThat(savedProgramProduct.getId(), is(programProduct.getId()));
  }

  @Test
  public void shouldGetByProductCode() throws Exception {

    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);
    Program hiv = new Program(1L);
    hiv.setCode("HIV");

    ProgramProduct programProduct2 = new ProgramProduct(hiv, product, 10, true);

    programProductMapper.insert(programProduct);
    programProductMapper.insert(programProduct2);

    List<ProgramProduct> returnedProducts = programProductMapper.getByProductCode(programProduct.getProduct().getCode());

    assertThat(returnedProducts.size(), is(2));
    assertContainsProgramProduct(returnedProducts, programProduct);
    assertContainsProgramProduct(returnedProducts, programProduct2);
  }

  @Test
  public void shouldGetByProgramProductIdAndFacilityTypeCode() throws Exception {

    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);

    programProductMapper.insert(programProduct);

    String facilityTypeCode = "warehouse";

    FacilityTypeApprovedProduct facilityTypeApprovedProduct = new FacilityTypeApprovedProduct();

    FacilityType facilityType = new FacilityType(facilityTypeCode);
    facilityType.setId(1L);

    facilityTypeApprovedProduct.setFacilityType(facilityType);
    facilityTypeApprovedProduct.setProgramProduct(programProduct);

    facilityApprovedProductMapper.insert(facilityTypeApprovedProduct);

    List<ProgramProduct> returnedProducts = programProductMapper.getByProgramIdAndFacilityCode(program.getId(), facilityTypeCode);

    assertThat(returnedProducts.size(), is(1));
    assertContainsProgramProduct(returnedProducts, programProduct);
  }

  @Test
  public void shouldGetAllProgramProductsForProgramIdWhenFacilityTypeCodeIsNull() throws Exception {

    ProgramProduct programProduct = new ProgramProduct(program, product, 10, true);

    Product product1 = make(a(ProductBuilder.defaultProduct, with(code, "P1")));
    product1.setCategory(product.getCategory());
    productMapper.insert(product1);

    ProgramProduct programProduct2 = new ProgramProduct(program, product1, 10, true);

    programProductMapper.insert(programProduct);
    programProductMapper.insert(programProduct2);

    List<ProgramProduct> returnedProducts = programProductMapper.getByProgramIdAndFacilityCode(program.getId(), null);

    assertThat(returnedProducts.size(), is(2));
    assertContainsProgramProduct(returnedProducts, programProduct);
    assertContainsProgramProduct(returnedProducts, programProduct2);
  }

  private void assertContainsProgramProduct(List<ProgramProduct> returnedProducts, final ProgramProduct programProduct) {
    boolean exists = exists(returnedProducts, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        ProgramProduct productFromCollection = (ProgramProduct) o;
        return (productFromCollection.getProgram().getCode().equals(programProduct.getProgram().getCode())) &&
          (productFromCollection.isActive() == programProduct.isActive()) &&
          (productFromCollection.getProduct().getActive() == programProduct.getProduct().getActive());
      }
    });

    assertTrue(exists);
  }
}
