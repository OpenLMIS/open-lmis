/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductPersistenceHandlerTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  private ProductPersistenceHandler productPersistenceHandler;
  @Mock
  private ProductService productService;

  @Before
  public void setUp() throws Exception {
    productPersistenceHandler = new ProductPersistenceHandler(productService);
  }

  @Test
  public void shouldSaveImportedProduct() throws Exception {
    Product product = new Product();
    productPersistenceHandler.save(product);
    verify(productService).save(product);
  }
}



