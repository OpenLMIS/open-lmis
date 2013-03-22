/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class ProductService {

  private ProductRepository repository;
  private ProductCategoryService categoryService;
  public static final String INVALID_PRODUCT_CATEGORY_CODE = "product.reference.category.invalid";

  @Autowired
  public ProductService(ProductRepository repository, ProductCategoryService categoryService) {
    this.repository = repository;
    this.categoryService = categoryService;
  }

  public void save(Product product) {
    validateAndSetProductCategory(product);
    repository.insert(product);
  }


  private void validateAndSetProductCategory(Product product) {
    ProductCategory category = product.getCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Integer categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException(INVALID_PRODUCT_CATEGORY_CODE);
    }
    category.setId(categoryId);
  }

  public Integer getIdForCode(String code) {
    return repository.getIdByCode(code);
  }
}
