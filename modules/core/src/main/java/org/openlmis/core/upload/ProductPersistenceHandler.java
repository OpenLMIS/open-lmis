/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("productPersistenceHandler")
public class ProductPersistenceHandler extends AbstractModelPersistenceHandler {

  private ProductService productService;

  @Autowired
  public ProductPersistenceHandler(ProductService productService) {
    this.productService = productService;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    return productService.getByCode(((Product)importable).getCode());
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    Product product = (Product) currentRecord;
    product.setModifiedBy(auditFields.getUser());
    product.setModifiedDate(auditFields.getCurrentTimestamp());
    if (existingRecord != null) product.setId(((Product)existingRecord).getId());
    productService.save(product);
  }

  @Override
  protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
    Product savedProduct = (Product) importable;
    if (savedProduct != null && savedProduct.getModifiedDate().equals(auditFields.getCurrentTimestamp())) {
      throw new DataException("Duplicate Product Code");
    }
  }
}
