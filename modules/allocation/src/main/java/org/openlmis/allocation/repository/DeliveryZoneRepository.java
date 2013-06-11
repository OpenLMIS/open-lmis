/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.repository;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.repository.mapper.DeliveryZoneMapper;
import org.openlmis.core.domain.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeliveryZoneRepository {

  @Autowired
  DeliveryZoneMapper mapper;

  public void insert(DeliveryZone zone) {
    mapper.insert(zone);
  }

  public void update(DeliveryZone zone) {
    mapper.update(zone);
  }

  public DeliveryZone getByCode(String code) {
    return mapper.getByCode(code);
  }

  public List<DeliveryZone> getByUserForRight(long userId, Right right) {
    return mapper.getByUserForRight(userId, right);
  }
}
