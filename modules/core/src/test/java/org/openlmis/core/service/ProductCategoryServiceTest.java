/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.openlmis.db.categories.UnitTests;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryServiceTest {

  @Mock
  private ProductCategoryRepository repository;

  @Autowired
  ProductCategoryService service;

  @Before
  public void setUp() throws Exception {
    service = new ProductCategoryService(repository);
  }

  @Test
  public void shouldGetProductCategoryIdByCode() {
    String categoryCode = "category code";
    Long categoryId = 1L;
    when(repository.getIdByCode(categoryCode)).thenReturn(categoryId);
    Long productCategoryIdByCode = service.getProductCategoryIdByCode(categoryCode);

    verify(repository).getIdByCode(categoryCode);
    assertThat(productCategoryIdByCode, is(categoryId));
  }

  @Test
  public void shouldUpdateProductCategoryIfAlreadyExists() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setId(1L);
    service.save(productCategory);
    verify(repository).update(productCategory);
    verify(repository, never()).insert(productCategory);
  }

  @Test
  public void shouldGetAll() {
    List<ProductCategory> categories = asList(new ProductCategory());
    when(repository.getAll()).thenReturn(categories);
    List<ProductCategory> returnedCategories = service.getAll();

    verify(repository).getAll();
    assertThat(returnedCategories, is(categories));
  }
}
