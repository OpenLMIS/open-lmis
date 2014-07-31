/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductGroupRepository is Repository class for ProductGroup related database operations.
 */

@Repository
@NoArgsConstructor
public class ProductGroupRepository {

  @Autowired
  ProductGroupMapper mapper;

  public void insert(ProductGroup productGroup) {
    try {
      mapper.insert(productGroup);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("error.duplicate.product.group.code");
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("error.reference.data.missing");
      } else {
        throw new DataException("error.incorrect.length");
      }
    }
  }

  public ProductGroup getByCode(String code) {
    return mapper.getByCode(code);
  }

  public void update(ProductGroup productGroup) {
    mapper.update(productGroup);
  }

  public List<ProductGroup> getAll() {
    return mapper.getAll();
  }
}
