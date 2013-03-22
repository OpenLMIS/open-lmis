/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.repository.ProductRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

  @Mock
  private ProductCategoryService categoryService;

  @Test
    public void shouldStoreProduct() throws Exception {
        Product product = new Product();
        ProductRepository productRepository = mock(ProductRepository.class);

        new ProductService(productRepository, categoryService).save(product);

        verify(productRepository).insert(product);

    }
}
