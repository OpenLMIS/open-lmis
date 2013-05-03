/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductCategoryMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.repository.ProductCategoryRepository.DUPLICATE_CATEGORY_NAME;

@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryRepositoryTest {

  @Mock
  private ProductCategoryMapper productCategoryMapper;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private ProductCategoryRepository productCategoryRepository;


  @Before
  public void setUp() throws Exception {
    productCategoryRepository = new ProductCategoryRepository(productCategoryMapper);
  }

  @Test
  public void shouldThrowExceptionIfInsertingProductCategoryWithDuplicateName() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DuplicateKeyException("some exception")).when(productCategoryMapper).insert(productCategory);
    expectedException.expect(DataException.class);
    expectedException.expectMessage(DUPLICATE_CATEGORY_NAME);

    productCategoryRepository.insert(productCategory);

  }


  @Test
  public void shouldThrowExceptionIfDataLengthIsIncorrectWhileInsertingProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DataIntegrityViolationException("some error")).when(productCategoryMapper).insert(productCategory);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Incorrect data length");

    productCategoryRepository.insert(productCategory);
  }

  @Test
  public void shouldThrowExceptionIfMissingCategoryNameWhileInsertingProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(productCategoryMapper).insert(productCategory);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Missing/Invalid Reference data");

    productCategoryRepository.insert(productCategory);
  }

  @Test
  public void shouldInsertProductCategoryIfDoesNotExist() {
    ProductCategory productCategory = new ProductCategory();
    when(productCategoryMapper.getProductCategoryByCode(productCategory.getCode())).thenReturn(null);
    productCategoryRepository.insert(productCategory);
    verify(productCategoryMapper).insert(productCategory);
  }

  @Test
  public void shouldGetCategoryIdByCode(){
    String categoryCode = "category code";
    Long categoryId = 1L;
    when(productCategoryMapper.getProductCategoryIdByCode(categoryCode)).thenReturn(categoryId);
    Long returnedCategoryId = productCategoryRepository.getProductCategoryIdByCode(categoryCode);

    verify(productCategoryMapper).getProductCategoryIdByCode(categoryCode);
    assertThat(returnedCategoryId, is(categoryId));
  }
}
