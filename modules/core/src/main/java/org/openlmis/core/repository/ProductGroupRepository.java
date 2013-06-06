/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ProductGroupRepository {

  ProductGroupMapper mapper;

  @Autowired
  public ProductGroupRepository(ProductGroupMapper mapper) {
    this.mapper = mapper;
  }

  public void insert(ProductGroup productGroup) {
    try {
      mapper.insert(productGroup);
    } catch (DuplicateKeyException duplicateKeyException) {
      throw new DataException("Duplicate Product Group Code Found");
    } catch (DataIntegrityViolationException dataIntegrityViolationException) {
      String errorMessage = dataIntegrityViolationException.getMessage().toLowerCase();
      if (errorMessage.contains("foreign key") || errorMessage.contains("violates not-null constraint")) {
        throw new DataException("Missing/Invalid Reference data");
      } else {
        throw new DataException("Incorrect data length");
      }
    }
  }

  public ProductGroup getByCode(String code) {
    return mapper.getByCode(code);
  }

  public void update(ProductGroup productGroup) {
    mapper.update(productGroup);
  }
}
