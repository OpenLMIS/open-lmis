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
import org.openlmis.core.domain.ProductCategory;
import org.openlmis.core.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling ProductCategory entity.
 */

@Service
@NoArgsConstructor
public class ProductCategoryService {

  private ProductCategoryRepository repository;

  @Autowired
  public ProductCategoryService(ProductCategoryRepository repository) {
    this.repository = repository;
  }

  public Long getProductCategoryIdByCode(String code) {
    return repository.getIdByCode(code);
  }

  public ProductCategory getByCode(String code) {
    return repository.getByCode(code);
  }

  public void save(ProductCategory productCategory) {
    if (productCategory.getId() != null) {
      repository.update(productCategory);
      return;
    }
    repository.insert(productCategory);
  }

  public ProductCategory getExisting(ProductCategory productCategory) {
    return repository.getExisting(productCategory);
  }

  public ProductCategory getById(Long id){
    return repository.getById(id);
  }

  public List<ProductCategory> getAll() {
    return repository.getAll();
  }
}
