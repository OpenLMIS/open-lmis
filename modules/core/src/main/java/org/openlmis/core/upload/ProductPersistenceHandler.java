/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.core.service.ProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProgramService programService;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return productService.getByCode(((Product) record).getCode());
  }

  @Override
  protected void save(BaseModel record) {
    productService.save((Product) record);
  }

  @Override
  public void postProcess(AuditFields auditFields) {
    programService.notifyProgramChange();
  }

  @Override
  protected String getDuplicateMessageKey() {
    return "error.duplicate.product.code";
  }

}
