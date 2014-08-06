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
import org.openlmis.core.domain.ProductForm;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.ProductFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository relating to persisting {@link org.openlmis.core.domain.ProductForm} entities.
 */
@Repository
@NoArgsConstructor
public class ProductFormRepository {

  @Autowired
  private ProductFormMapper pfMapper;

  ProductFormRepository(ProductFormMapper productFormMapper) {this.pfMapper = productFormMapper;}

  /**
   * Gets existing {@link org.openlmis.core.domain.ProductForm} by it's code.
   * @param code the ProductForm's code.
   * @return The ProductForm that has that code, or null if no such ProductForm
   * exists with the given code.
   */
  public ProductForm getByCode(String code) {return pfMapper.getByCode(code);}

  /**
   * Insert a new {@link org.openlmis.core.domain.ProductForm}.
   * @param pf {@link org.openlmis.core.domain.ProductForm} to insert.
   * @throws DataException if entity is invalid or already exists.
   */
  public void insert(ProductForm pf) {
    pf.isValid();
    if(getByCode(pf.getCode()) != null) throw new DataException("error.duplicate.dosage.unit.code");

    try {
      pfMapper.insert(pf);
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length", dive);
    }
  }
  
  public List<ProductForm> getAll() {
    return pfMapper.getAll();
  }

  /**
   * Updates an existing {@link org.openlmis.core.domain.ProductForm}.
   * @param pf {@link org.openlmis.core.domain.ProductForm} to update.
   * @throws DataException if entity is invalid.
   */
  public void update(ProductForm pf) {
    pf.isValid();

    try {
      pfMapper.update(pf);
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length", dive);
    }
  }
}