/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductGroupRepository;
import org.openlmis.core.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling Product entity.
 */

@Service
@NoArgsConstructor
public class ProductService {

  @Autowired
  private ProductRepository repository;

  @Autowired
  private ProductGroupRepository productGroupRepository;

  @Autowired
  private ProductCategoryService categoryService;

  @Autowired
  ProgramProductService programProductService;

  @Autowired
  ProgramService programService;

  public void save(Product product) {

    product.validate();
    validateAndSetProductCategory(product);

    if (product.getId() == null) {
      repository.insert(product);
      return;
    }

    setReferenceDataForProduct(product);

    List<ProgramProduct> existingProgramProducts = programProductService.getByProductCode(product.getCode());

    repository.update(product);

    notifyProgramCatalogChange(product, existingProgramProducts);
  }

  private void notifyProgramCatalogChange(Product product, List<ProgramProduct> existingProgramProducts) {
    for (ProgramProduct existingProgramProduct : existingProgramProducts) {
      if (existingProgramProduct.isActive() && (existingProgramProduct.getProduct().getActive() != product.getActive())) {
        programService.setFeedSendFlag(existingProgramProduct.getProgram(), true);
      }
    }
  }

  private void setReferenceDataForProduct(Product product) {
    if (product.getForm() != null) {
      product.getForm().setId(repository.getProductFormIdForCode(product.getForm().getCode()));
    }
    if (product.getDosageUnit() != null) {
      product.getDosageUnit().setId(repository.getDosageUnitIdForCode(product.getDosageUnit().getCode()));
    }
    if (product.getProductGroup() != null) {
      ProductGroup productGroup = productGroupRepository.getByCode(product.getProductGroup().getCode());
      if (productGroup == null) throw new DataException("error.reference.data.invalid.product.group");
      product.getProductGroup().setId(productGroup.getId());

    }
  }

  private void validateAndSetProductCategory(Product product) {
    ProductCategory category = product.getCategory();
    if (category == null) return;
    String categoryCode = category.getCode();
    if (categoryCode == null || categoryCode.isEmpty()) return;
    Long categoryId = categoryService.getProductCategoryIdByCode(category.getCode());
    if (categoryId == null) {
      throw new DataException("error.reference.data.invalid.product");
    }
    category.setId(categoryId);
  }

  public Long getIdForCode(String code) {
    return repository.getIdByCode(code);
  }

  public Product getByCode(String code) {
    return repository.getByCode(code);
  }

  public boolean isActive(String code) {
    return repository.isActive(code);
  }
}
