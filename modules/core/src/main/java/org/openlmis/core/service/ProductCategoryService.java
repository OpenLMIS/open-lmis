/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.openlmis.upload.Importable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ProductCategoryService {


  private ProductCategoryRepository productCategoryRepository;

  @Autowired
  public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
    this.productCategoryRepository = productCategoryRepository;
  }

  public Integer getProductCategoryIdByCode(String code) {
    return productCategoryRepository.getProductCategoryIdByCode(code);
  }

  public ProductCategory getByCode(String code) {
    return productCategoryRepository.getByCode(code);
  }

  public void save(ProductCategory productCategory) {
    productCategoryRepository.save(productCategory);
  }
}
