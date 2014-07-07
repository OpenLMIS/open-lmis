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
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing persistence of {@link org.openlmis.core.domain.FacilityType} entities and related operations.
 */
@Repository
@NoArgsConstructor
public class FacilityTypeRepository {
  private FacilityTypeMapper mapper;

  @Autowired
  public FacilityTypeRepository(FacilityTypeMapper facilityTypeMapper) {
    this.mapper = facilityTypeMapper;
  }

  /**
   * Gets a FacilityType by it's associated code.
   * @param code the code of the FacilityType.
   * @return the FacilityType with the given code or null if no FacilityType exists with the given code.
   */
  public FacilityType getByCode(String code) {
    if(code == null) return null;
    return mapper.getByCode(code);
  }

  /**
   * Saves the given FacilityType to persistent storage.
   * @param facilityType the FacilityType to save.
   * @throws java.lang.NullPointerException if facilityType is null.
   * @throws DataException if unable to save facilityType.
   */
  public void save(FacilityType facilityType) {
    if(facilityType == null) throw new NullPointerException("FacilityType argument is null");

    try {
      if(facilityType.hasId()) mapper.update(facilityType);
      else mapper.insert(facilityType);
    } catch(DuplicateKeyException dke) {
      throw new DataException("error.duplicate.facility.type.code", dke);
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length", dive);
    }
  }
}
