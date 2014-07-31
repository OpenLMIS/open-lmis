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
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.ProductGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling ProductGroup entity.
 */

@Service
@NoArgsConstructor
public class ProductGroupService {

  private ProductGroupRepository repository;

  @Autowired
  public ProductGroupService(ProductGroupRepository repository) {
    this.repository = repository;
  }

  public void save(ProductGroup productGroup) {
    if (productGroup.getId() == null) {
      repository.insert(productGroup);
    }
    repository.update(productGroup);
  }

  public ProductGroup getByCode(String code) {
    return repository.getByCode(code);
  }

  public List<ProductGroup> getAll() {
    return repository.getAll();
  }

  public ProductGroup validateAndReturn(ProductGroup group) {
    if (group == null) return null;

    String productGroupCode = group.getCode();
    if (productGroupCode == null || productGroupCode.isEmpty()) return null;

    group = repository.getByCode(productGroupCode);
    if (group == null) throw new DataException("error.reference.data.invalid.product.group");
    return group;
  }
}
