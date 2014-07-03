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
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing persistent storage/retrieval of {@link org.openlmis.core.domain.GeographicLevel} entities.
 */
@Repository
@NoArgsConstructor
public class GeographicLevelRepository {
  private GeographicLevelMapper mapper;

  @Autowired
  public GeographicLevelRepository(GeographicLevelMapper mapper) {
    this.mapper = mapper;
  }

  public int getLowestGeographicLevel() {return mapper.getLowestGeographicLevel();}

  public GeographicLevel getGeographicLevelByCode(String code) {return mapper.getGeographicLevelByCode(code);}

  public List<GeographicLevel> getAll() {return mapper.getAll();}

  public GeographicLevel getByCode(String code) {return mapper.getByCode(code);}

  /**
   * Saves the given {@link org.openlmis.core.domain.GeographicLevel}.  If the GeographicLevel already exists,
   * then it will attempt to update it.
   * @param geographicLevel the geographic level to save.
   * @throws DataException if unable - malformed or attempting to save a new geographic level with a duplicate identity.
   * i.e. {@link #getByCode(String)} is not null.
   */
  public void save(GeographicLevel geographicLevel) {
    try {
      if(geographicLevel.hasId())
        mapper.update(geographicLevel);
      else
        mapper.insert(geographicLevel);
    } catch(DuplicateKeyException dke) {
      throw new DataException("error.duplicate.geographic.level.code");
    } catch(DataIntegrityViolationException dive) {
      throw new DataException("error.incorrect.length");
    }
  }
}
