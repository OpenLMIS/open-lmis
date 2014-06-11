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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryServiceTest {

  @Mock
  private ProductCategoryRepository productCategoryRepository;

  @Autowired
  ProductCategoryService productCategoryService;

  @Before
  public void setUp() throws Exception {
    productCategoryService = new ProductCategoryService(productCategoryRepository);
  }

  @Test
  public void shouldGetProductCategoryIdByCode() {
    String categoryCode = "category code";
    Long categoryId = 1L;
    when(productCategoryRepository.getIdByCode(categoryCode)).thenReturn(categoryId);
    Long productCategoryIdByCode = productCategoryService.getProductCategoryIdByCode(categoryCode);

    verify(productCategoryRepository).getIdByCode(categoryCode);
    assertThat(productCategoryIdByCode, is(categoryId));
  }

  @Test
  public void shouldUpdateProductCategoryIfAlreadyExists() {
    ProductCategory productCategory = new ProductCategory();
    productCategory.setId(1L);
    productCategoryService.save(productCategory);
    verify(productCategoryRepository).update(productCategory);
    verify(productCategoryRepository, never()).insert(productCategory);
  }

}
