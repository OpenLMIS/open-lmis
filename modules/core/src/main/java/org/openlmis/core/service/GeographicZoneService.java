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

    validateAndSetParent(geographicZone);

    if (geographicZone.getId() == null) {
      repository.insert(geographicZone);
      return;
    }
    repository.update(geographicZone);
  }

  private void validateAndSetParent(GeographicZone geographicZone) {
    geographicZone.setLevel(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode()));
    if (geographicZone.getLevel() == null)
      throw new DataException("error.geo.level.invalid");
    if (geographicZone.getParent() == null) {
      geographicZone.setParent(repository.getByCode("Root"));
      return;
    }
    geographicZone.setParent(repository.getByCode(geographicZone.getParent().getCode()));
    if (geographicZone.getParent() == null)
      throw new DataException("error.geo.zone.parent.invalid");
  }

  public GeographicZone getByCode(GeographicZone geographicZone) {
    return repository.getByCode(geographicZone.getCode());
  }

  public GeographicZone getById(long id) {
    return repository.getById(id);
  }
}
