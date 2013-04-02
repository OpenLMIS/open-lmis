/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Test;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.openlmis.core.service.ProductCategoryService;
import org.openlmis.upload.model.AuditFields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProductCategoryPersistenceHandlerTest {



  @Test
  public void shouldSaveImportedProductCategory() throws Exception {
    ProductCategoryService productCategoryService = mock(ProductCategoryService.class);
    ProductCategory productCategory = new ProductCategory();

    new ProductCategoryPersistenceHandler(productCategoryService).execute(productCategory, 0, new AuditFields(1, null));
    assertThat(productCategory.getModifiedBy(), is(1));
    assertThat(productCategory.getModifiedDate(), is(notNullValue()));
    verify(productCategoryService).save(productCategory);
  }
}

