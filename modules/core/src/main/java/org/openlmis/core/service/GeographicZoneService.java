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
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Exposes the services for handling GeographicZone entity.
 */

@Service
@NoArgsConstructor
public class GeographicZoneService {

  @Autowired
  GeographicZoneRepository repository;

  public void save(GeographicZone geographicZone) {
    geographicZone.validateMandatoryFields();
    geographicZone.setLevel(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode()));
    geographicZone.validateLevel();

    if (!geographicZone.isRootLevel()) {
      geographicZone.setParent(repository.getByCode(geographicZone.getParent().getCode()));
      geographicZone.validateParentExists();
      geographicZone.validateParentIsHigherInHierarchy();
    }

    repository.save(geographicZone);
  }

  public GeographicZone getByCode(GeographicZone geographicZone) {
    return repository.getByCode(geographicZone.getCode());
  }

  public GeographicZone getById(Long id) {
    return repository.getById(id);
  }

  public List<GeographicZone> searchBy(String searchParam, String columnName) {
    return repository.searchBy(searchParam, columnName);
  }

  public List<GeographicLevel> getAllGeographicLevels() {
    return repository.getAllGeographicLevels();
  }

  public List<GeographicZone> getAllGeographicZonesAbove(GeographicLevel level) {
    return repository.getAllGeographicZonesAbove(level);
  }
}
