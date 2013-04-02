/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    Product existingRecord = null;
    productPersistenceHandler.save(existingRecord, product, new AuditFields(1, new Date()));
    assertThat(product.getModifiedBy(), is(1));
    assertThat(product.getModifiedDate(), is(notNullValue()));
    verify(productService).save(product);
  }

  @Test
  public void shouldThrowErrorIfDuplicateCodeFoundWithSameTimeStamp() {
    Product product = new Product();
    product.setCode("P1");
    Date currentTime = new Date();
    product.setModifiedDate(currentTime);

    AuditFields auditFields  = new AuditFields();
    auditFields.setCurrentTimestamp(currentTime);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Product Code");

    productPersistenceHandler.throwExceptionIfAlreadyProcessedInCurrentUpload(product, auditFields);
  }

}



