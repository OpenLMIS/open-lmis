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
import org.openlmis.core.repository.ProductCategoryRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryServiceTest {

  @Mock
  private ProductCategoryRepository productCategoryRepository;

  @Test
  public void shouldGetProductCategoryIdByCode(){
    ProductCategoryService productCategoryService = new ProductCategoryService(productCategoryRepository);
    String categoryCode = "category code";
    Integer categoryId = 1;
    when(productCategoryRepository.getProductCategoryIdByCode(categoryCode)).thenReturn(categoryId);
    Integer productCategoryIdByCode = productCategoryService.getProductCategoryIdByCode(categoryCode);

    verify(productCategoryRepository).getProductCategoryIdByCode(categoryCode);
    assertThat(productCategoryIdByCode, is(categoryId));
  }

}
