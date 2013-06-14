/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ProductCategoryRepository {


  private ProductCategoryMapper categoryMapper;
  public static final String DUPLICATE_CATEGORY_NAME = "product.category.name.duplicate";

  @Autowired
  public ProductCategoryRepository(ProductCategoryMapper categoryMapper) {
    this.categoryMapper = categoryMapper;
  }

  public void insert(ProductCategory productCategory) {

    try {
      categoryMapper.insert(productCategory);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException(DUPLICATE_CATEGORY_NAME);
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("error.reference.data.missing");
      } else {
        throw new DataException("error.incorrect.length");
      }
    }
  }

  public void update(ProductCategory productCategory) {
    categoryMapper.update(productCategory);
  }

  public ProductCategory getExisting(ProductCategory productCategory) {
    return categoryMapper.getProductCategoryByCode(productCategory.getCode());
  }

  public Long getProductCategoryIdByCode(String categoryCode) {
    return categoryMapper.getProductCategoryIdByCode(categoryCode);
  }

  public ProductCategory getByCode(String code) {
    return categoryMapper.getProductCategoryByCode(code);
  }
}
