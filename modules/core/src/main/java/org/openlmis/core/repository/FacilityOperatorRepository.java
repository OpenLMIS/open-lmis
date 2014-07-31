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
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityOperatorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link org.openlmis.core.domain.FacilityOperator} entities and related operations.
 */
@Repository
@NoArgsConstructor
public class FacilityOperatorRepository {
  private FacilityOperatorMapper facilityOperatorMapper;

  @Autowired
  public FacilityOperatorRepository(FacilityOperatorMapper facilityOperatorMapper) {
    this.facilityOperatorMapper = facilityOperatorMapper;
  }

  /**
   * Gets the FacilityOperator that has the given code.
   * @param code the code, case insensitive
   * @return the FacilityOperator with the given code or null if no such FacilityOperator exists with the given code.
   */
  public FacilityOperator getByCode(String code) {
    if(code == null) return null;
    return facilityOperatorMapper.getByCode(code);
  }

  /**
   * Saves a FacilityOperator entity to persistent storage.  If
   * {@link org.openlmis.core.domain.FacilityOperator#hasId()} is <code>true</code>, then the id will be used to
   * update the existing entity, otherwise a new one will be created.
   * @param facilityOperator the FacilityOperator to save.
   * @throws org.openlmis.core.exception.DataException if unable to save entity.
   * @throws java.lang.NullPointerException if facilityOperator is null.
   */
  public void save(FacilityOperator facilityOperator) {
    if(facilityOperator == null) throw new NullPointerException("facilityOperator argument is null");

    try {
      if(facilityOperator.hasId()) facilityOperatorMapper.update(facilityOperator);
      else facilityOperatorMapper.insert(facilityOperator);
    } catch(DuplicateKeyException dke) {
      throw new DataException("error.duplicate.facility.operator.code");
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length");
    }
  }

  /**
   * Gets the FacilityOperator by it's persistence id.
   * @param id the id of the FacilityOperator to find
   * @return the FacilityOperator with the given id or null if no such FacilityOperator exists with given id.
   */
  public FacilityOperator getById(long id) {
    return facilityOperatorMapper.getById(id);
  }

  /**
   * Gets all the FacilityOperators in descending order by their display order.
   * @return a list of all persisted FacilityOperator entities.
   */
  public List<FacilityOperator> getAll() {
    return facilityOperatorMapper.getAll();
  }
}