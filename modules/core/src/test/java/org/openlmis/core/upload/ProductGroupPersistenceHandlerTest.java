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
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.service.ProductGroupService;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductGroupPersistenceHandlerTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  private ProductGroupPersistenceHandler productGroupPersistenceHandler;

  @Mock
  private ProductGroupService productGroupService;

  @Before
  public void setUp() throws Exception {
    productGroupPersistenceHandler = new ProductGroupPersistenceHandler(productGroupService);
  }

  @Test
  public void shouldSaveImportedProductGroup() throws Exception {
    ProductGroup productGroup = new ProductGroup();
    productGroupPersistenceHandler.save(productGroup);
    verify(productGroupService).save(productGroup);
  }
}
