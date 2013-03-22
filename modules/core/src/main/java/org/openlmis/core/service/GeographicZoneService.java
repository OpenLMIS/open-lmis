/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
    GeographicZone savedZone = repository.getByCode(geographicZone.getCode());
    if (savedZone != null && savedZone.getModifiedDate().equals(geographicZone.getModifiedDate()))
      throw new DataException("Duplicate Geographic Zone Code");

    validateAndSetParent(geographicZone);

    if (savedZone == null) {
      repository.insert(geographicZone);
      return;
    }

    geographicZone.setId(savedZone.getId());
    repository.update(geographicZone);
  }

  private void validateAndSetParent(GeographicZone geographicZone) {
    geographicZone.setLevel(repository.getGeographicLevelByCode(geographicZone.getLevel().getCode()));
    if (geographicZone.getLevel() == null)
      throw new DataException("Invalid Geographic Level Code");
    if (geographicZone.getParent() == null) {
      geographicZone.setParent(repository.getByCode("Root"));
      return;
    }
    geographicZone.setParent(repository.getByCode(geographicZone.getParent().getCode()));
    if (geographicZone.getParent() == null)
      throw new DataException("Invalid Geographic Zone Parent Code");
  }
}
