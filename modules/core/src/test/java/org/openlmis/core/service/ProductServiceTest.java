/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductRepository;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {


  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Mock
  private ProductCategoryService categoryService;

  @Mock
  private ProductRepository productRepository;

  private ProductService productService;


  @Before
  public void setUp() throws Exception {
    productService = new ProductService(productRepository, categoryService, null);
  }

  @Test
  public void shouldStoreProduct() throws Exception {
    Product product = new Product();

    productService.save(product);

    verify(productRepository).insert(product);

  }

  @Test
  public void shouldThrowExceptionIfCategoryDoesNotExist() {
    Product product = new Product();
    ProductCategory category = new ProductCategory();
    category.setCode("Invalid Code");
    product.setCategory(category);
    when(categoryService.getProductCategoryIdByCode("Invalid Code")).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.reference.data.invalid.product");

    productService.save(product);
  }

  @Test
  public void shouldInsertProductIfNotPresent() throws Exception {
    Product product = new Product();
    product.setCode("P1");

    when(productRepository.getByCode("P1")).thenReturn(null);

    productService.save(product);

    verify(productRepository).insert(product);
  }

  @Test
  public void shouldUpdateProductIfPresent() {
    Product product = new Product();
    product.setId(2L);

    productService.save(product);

    verify(productRepository).update(product);
  }
}
