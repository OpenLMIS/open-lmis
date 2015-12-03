/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ProductCategoryPersistenceHandler is used for uploads of ProductCategory. It uploads each ProductCategory
 * record by record.
 */
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
