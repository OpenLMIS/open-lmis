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
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.GeographicLevelMapper;
import org.openlmis.core.repository.mapper.GeographicZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * GeographicZoneRepository is repository class for GeographicZone related database operations.
 */

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

  public GeographicZone getByCode(String code) {
    return mapper.getGeographicZoneByCode(code);
  }

  public Integer getLowestGeographicLevel() {
    return geographicLevelMapper.getLowestGeographicLevel();
  }

  public List<GeographicZone> getAllGeographicZones() {
    return mapper.getAllGeographicZones();
  }

  public void save(GeographicZone zone) {
    try {
      if (zone.getId() == null) {
        mapper.insert(zone);
        return;
      }
      mapper.update(zone);
    } catch (DuplicateKeyException e) {
      throw new DataException("error.duplicate.geographic.zone.code");
    } catch (DataIntegrityViolationException e) {
      throw new DataException("error.incorrect.length");
    }
  }

  public GeographicLevel getGeographicLevelByCode(String code) {
    return mapper.getGeographicLevelByCode(code);
  }

  public GeographicZone getById(Long id) {
    return mapper.getWithParentById(id);
  }

  public List<GeographicZone> searchByParentName(String searchParam, Pagination pagination) {
    return mapper.searchByParentName(searchParam, pagination);
  }

  public List<GeographicZone> searchByName(String searchParam, Pagination pagination) {
    return mapper.searchByName(searchParam, pagination);
  }

  public List<GeographicLevel> getAllGeographicLevels() {
    return geographicLevelMapper.getAll();
  }

  public List<GeographicZone> getAllGeographicZonesAbove(GeographicLevel geographicLevel) {
    return mapper.getAllGeographicZonesAbove(geographicLevel);
  }

  public Integer getTotalParentSearchResultCount(String param) {
    return mapper.getTotalParentSearchResultCount(param);
  }

  public Integer getTotalSearchResultCount(String param) {
    return mapper.getTotalSearchResultCount(param);
  }

  public List<GeographicZone> getGeographicZonesByCodeOrName(String searchParam) {
    return mapper.getGeographicZonesByCodeOrName(searchParam);
  }

  public Integer getGeographicZonesCountBy(String searchParam) {
    return mapper.getGeographicZonesCountBy(searchParam);
  }
}
