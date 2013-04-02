/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductCategoryService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("productCategoryPersistenceHandler")
public class ProductCategoryPersistenceHandler  extends AbstractModelPersistenceHandler {

  ProductCategoryService productCategoryService;

  @Autowired
  public ProductCategoryPersistenceHandler(ProductCategoryService productCategoryService) {
    this.productCategoryService = productCategoryService;
  }

  @Override
  protected Importable getExisting(Importable importable) {
    return productCategoryService.getByCode(((ProductCategory) importable).getCode());
  }

  @Override
  protected void save(Importable existingRecord, Importable modelClass, AuditFields auditFields) {
    ProductCategory productCategory = (ProductCategory) modelClass;
    productCategory.setModifiedBy(auditFields.getUser());
    productCategory.setModifiedDate(new Date());
    if (existingRecord != null) productCategory.setId(((ProductCategory) existingRecord).getId());
    productCategoryService.save(productCategory);
  }

  @Override
  protected void throwExceptionIfAlreadyProcessedInCurrentUpload(Importable importable, AuditFields auditFields) {
    ProductCategory productCategory = (ProductCategory) importable;
    if (productCategory.getModifiedDate().equals(auditFields.getCurrentTimestamp())) {
      throw new DataException("Duplicate Product Category");
    }
  }
}
