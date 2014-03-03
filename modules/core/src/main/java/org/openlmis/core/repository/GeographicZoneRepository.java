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
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class GeographicZoneRepository {

  private GeographicZoneMapper mapper;

  private GeographicLevelMapper geographicLevelMapper;

  @Autowired
  public GeographicZoneRepository(GeographicZoneMapper mapper, GeographicLevelMapper geographicLevelMapper) {
    this.mapper = mapper;
    this.geographicLevelMapper = geographicLevelMapper;
  }

    public void save(GeographicZone geographicZone) {
        try {
            validateAndSetGeographicZone(geographicZone);
            mapper.insert(geographicZone);
        } catch (DuplicateKeyException exception) {
            throw new DataException("Duplicate Geographic Zone Code");
        } catch (DataIntegrityViolationException exception) {
            throw new DataException("error.incorrect.length");
        }
    }

    private void validateAndSetGeographicZone(GeographicZone geographicZone) {
        geographicZone.setLevel(mapper.getGeographicLevelByCode(geographicZone.getLevel().getCode()));
        if (geographicZone.getLevel() == null)
            throw new DataException("Invalid Geographic Level Code");
        if (geographicZone.getParent() == null) {
            geographicZone.setParent(mapper.getGeographicZoneByCode("Root"));
            return;
        }
        geographicZone.setParent(mapper.getGeographicZoneByCode(geographicZone.getParent().getCode()));
        if (geographicZone.getParent() == null)
            throw new DataException("Invalid Geographic Zone Parent Code");
    }

    public GeographicZone getByCode(String code) {
    return mapper.getGeographicZoneByCode(code);
  }

  public Integer getLowestGeographicLevel() {
    return geographicLevelMapper.getLowestGeographicLevel();
  }

  public List<GeographicZone> getAllGeographicZones() {
    return mapper.getAllGeographicZones_Ext();
  }

  public GeographicLevel getGeographicLevelByCode(String code) {
    return mapper.getGeographicLevelByCode(code);
  }

  public GeographicZone getById(Long id) {
    return mapper.getWithParentById(id);
  }

  public List<GeographicZone> searchGeographicZone(String geographicZoneSearchParam) {
    return mapper.getGeographicZoneWithSearchedName(geographicZoneSearchParam);
  }

  public GeographicZone getById(int id){
    return mapper.getGeographicZoneById_Ext(id);
  }

  public void insert_Ext(GeographicZone geographicZone)
  {
    try {
      mapper.insert_Ext(geographicZone);
    }
    catch (DataIntegrityViolationException e) {
      throw new DataException("error.incorrect.length");
    }
  }

  public void update(GeographicZone zone) {
    try {
      mapper.update(zone);
    } catch (DataIntegrityViolationException e) {
      throw new DataException("error.incorrect.length");
    }
  }
}
