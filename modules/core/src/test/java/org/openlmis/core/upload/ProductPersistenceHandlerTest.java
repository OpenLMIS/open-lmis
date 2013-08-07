/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.model.AuditFields;

import static org.mockito.Mockito.verify;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProductPersistenceHandlerTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private ProductService productService;

  @Mock
  ProgramService programService;

  @InjectMocks
  private ProductPersistenceHandler productPersistenceHandler;

  @Test
  public void shouldSaveImportedProduct() throws Exception {
    Product product = new Product();
    productPersistenceHandler.save(product);
    verify(productService).save(product);
  }

  @Test
  public void shouldNotifyProgramServiceInPostProcess() throws Exception {
    productPersistenceHandler.postProcess(new AuditFields());

    verify(programService).notifyProgramChange();
  }
}



