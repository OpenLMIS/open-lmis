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
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class GeographicZoneService {

  @Autowired
  GeographicZoneRepository repository;

  public void save(GeographicZone geographicZone) {
    geographicZone.setLevel(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode()));

    validateLevel(geographicZone);
    validateLevelAndParentAssociation(geographicZone);

    geographicZone.setParent(repository.getByCode(geographicZone.getParent().getCode()));
    validateParent(geographicZone);

    repository.save(geographicZone);
  }

  public GeographicZone getByCode(GeographicZone geographicZone) {
    return repository.getByCode(geographicZone.getCode());
  }

  public GeographicZone getById(long id) {
    return repository.getById(id);
  }

  private void validateParent(GeographicZone geographicZone) {
    validateParentGeoZone(geographicZone);
    if (!geographicZone.isParentValid()) {
      throw new DataException("error.invalid.hierarchy");
    }
  }

  private void validateParentGeoZone(GeographicZone geographicZone) {
    if (geographicZone.getParent() == null) {
      throw new DataException("error.geo.zone.parent.invalid");
    }
  }

  private void validateLevelAndParentAssociation(GeographicZone geographicZone) {
    if (geographicZone.getParent() == null && !geographicZone.isRootLevel()) {
      throw new DataException("error.invalid.hierarchy");
    }

    if (geographicZone.getParent() != null && geographicZone.isRootLevel()) {
      throw new DataException("error.invalid.hierarchy");
    }
  }

  private void validateLevel(GeographicZone geographicZone) {
    if (geographicZone.getLevel() == null)
      throw new DataException("error.geo.level.invalid");
  }
}
