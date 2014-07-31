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
import org.openlmis.core.domain.RegimenCategory;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RegimenCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RegimenCategoryRepository {
  private RegimenCategoryMapper mapper;

  @Autowired
  public RegimenCategoryRepository(RegimenCategoryMapper regimenCategoryMapper) {
    this.mapper = regimenCategoryMapper;
  }

  /**
   * Gets a list of all stored RegimenCategory entities.
   * @return an sorted list by display order and then name, ascending.
   */
  public List<RegimenCategory> getAll() {
    return mapper.getAll();
  }

  /**
   * Finds a stored RegimenCategory with the given code.
   * @param code the code to find by, case insensitive
   * @return the RegimenCategory with the given code, or null if no such code exists.
   */
  public RegimenCategory getByCode(String code) {
    if(code == null) return null;
    return mapper.getByCode(code);
  }

  /**
   * Saves the given RegimenCategory entity if possible.
   * @param regimenCategory the RegimenCategory to save.
   * @throws java.lang.NullPointerException if regimenCategory is null.
   * @throws org.openlmis.core.exception.DataException if unable to save.
   */
  public void save(RegimenCategory regimenCategory) {
    if(regimenCategory == null) throw new NullPointerException("RegimenCategory argument is null");

    try {
      if(regimenCategory.hasId()) mapper.update(regimenCategory);
      else mapper.insert(regimenCategory);
    } catch (DuplicateKeyException dke) {
      throw new DataException("error.duplicate.regimen.category", dke);
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length", dive);
    }
  }
}
