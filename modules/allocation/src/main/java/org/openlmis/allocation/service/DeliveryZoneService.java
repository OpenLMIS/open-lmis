/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.service;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.repository.DeliveryZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryZoneService {

  @Autowired
  DeliveryZoneRepository repository;

  public void save(DeliveryZone zone) {
    if (zone.getId() != null)
      repository.update(zone);
    else
      repository.insert(zone);
  }

  public DeliveryZone getByCode(String code) {
    return repository.getByCode(code);
  }
}
