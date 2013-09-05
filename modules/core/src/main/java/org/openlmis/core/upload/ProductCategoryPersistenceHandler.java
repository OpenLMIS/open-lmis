/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryPersistenceHandler extends AbstractModelPersistenceHandler {

  ProductCategoryService productCategoryService;

  @Autowired
  public ProductCategoryPersistenceHandler(ProductCategoryService productCategoryService) {
    this.productCategoryService = productCategoryService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return productCategoryService.getExisting((ProductCategory) record);
  }

  @Override
  protected void save(BaseModel modelClass) {
    productCategoryService.save((ProductCategory) modelClass);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.product.category";
  }
}
