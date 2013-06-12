/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.service;

import org.apache.commons.collections.Predicate;
import org.openlmis.allocation.domain.DeliveryZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.exists;
import static org.openlmis.core.domain.Right.PLAN_DISTRIBUTION;

@Service
public class AllocationPermissionService {

  @Autowired
  DeliveryZoneService zoneService;

  public boolean hasPermissionOnZone(Long userId, final long zoneId) {
    List<DeliveryZone> zones = zoneService.getByUserForRight(userId, PLAN_DISTRIBUTION);

    return exists(zones, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        DeliveryZone zone = (DeliveryZone) o;
        return zone.getId() == zoneId;
      }
    });
  }
}
