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

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryRepositoryTest {

  @Mock
  private ProductCategoryMapper mapper;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private ProductCategoryRepository repository;


  @Before
  public void setUp() throws Exception {
    repository = new ProductCategoryRepository(mapper);
  }

  @Test
  public void shouldThrowExceptionIfInsertingProductCategoryWithDuplicateName() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DuplicateKeyException("some exception")).when(mapper).insert(productCategory);
    expectedException.expect(DataException.class);
    expectedException.expectMessage("product.category.name.duplicate");

    repository.insert(productCategory);

  }


  @Test
  public void shouldThrowExceptionIfDataLengthIsIncorrectWhileInsertingProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DataIntegrityViolationException("some error")).when(mapper).insert(productCategory);

    expectedException.expect(dataExceptionMatcher("error.incorrect.length"));

    repository.insert(productCategory);
  }

  @Test
  public void shouldThrowExceptionIfMissingCategoryNameWhileInsertingProductCategory() {
    ProductCategory productCategory = new ProductCategory();
    doThrow(new DataIntegrityViolationException("violates not-null constraint")).when(mapper).insert(
      productCategory);

    expectedException.expect(dataExceptionMatcher("error.reference.data.missing"));

    repository.insert(productCategory);
  }

  @Test
  public void shouldInsertProductCategoryIfDoesNotExist() {
    ProductCategory productCategory = new ProductCategory();
    when(mapper.getByCode(productCategory.getCode())).thenReturn(null);
    repository.insert(productCategory);
    verify(mapper).insert(productCategory);
  }

  @Test
  public void shouldGetCategoryIdByCode() {
    String categoryCode = "category code";
    Long categoryId = 1L;
    when(mapper.getIdByCode(categoryCode)).thenReturn(categoryId);
    Long returnedCategoryId = repository.getIdByCode(categoryCode);

    verify(mapper).getIdByCode(categoryCode);
    assertThat(returnedCategoryId, is(categoryId));
  }

  @Test
  public void shouldGetAll() {
    List<ProductCategory> categories = asList(new ProductCategory());
    when(mapper.getAll()).thenReturn(categories);
    List<ProductCategory> returnedCategories = repository.getAll();

    verify(mapper).getAll();
    assertThat(returnedCategories, is(categories));
  }
}
