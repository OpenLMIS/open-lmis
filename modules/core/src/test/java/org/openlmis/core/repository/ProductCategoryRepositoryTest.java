/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductCategoryMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
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
    expectedException.expectMessage("product.category.name.duplicate");

    productCategoryRepository.insert(productCategory);

  }


  @Test
  public void shouldThrowExceptionIfDataLengthIsIncorrectWhileInsertingProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DataIntegrityViolationException("some error")).when(productCategoryMapper).insert(productCategory);

    expectedException.expect(dataExceptionMatcher("error.incorrect.length"));

    productCategoryRepository.insert(productCategory);
  }

  @Test
  public void shouldThrowExceptionIfMissingCategoryNameWhileInsertingProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(productCategoryMapper).insert(
      productCategory);

    expectedException.expect(dataExceptionMatcher("error.reference.data.missing"));

    productCategoryRepository.insert(productCategory);
  }

  @Test
  public void shouldInsertProductCategoryIfDoesNotExist() {
    ProductCategory productCategory = new ProductCategory();
    when(productCategoryMapper.getByCode(productCategory.getCode())).thenReturn(null);
    productCategoryRepository.insert(productCategory);
    verify(productCategoryMapper).insert(productCategory);
  }

  @Test
  public void shouldGetCategoryIdByCode() {
    String categoryCode = "category code";
    Long categoryId = 1L;
    when(productCategoryMapper.getIdByCode(categoryCode)).thenReturn(categoryId);
    Long returnedCategoryId = productCategoryRepository.getIdByCode(categoryCode);

    verify(productCategoryMapper).getIdByCode(categoryCode);
    assertThat(returnedCategoryId, is(categoryId));
  }
}
