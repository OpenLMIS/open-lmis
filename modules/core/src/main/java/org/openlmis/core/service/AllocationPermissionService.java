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

import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.DeliveryZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.exists;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;

/**
 * Exposes the services for determining user permissions on DeliveryZone entity.
 */

@Service
public class AllocationPermissionService {

  @Autowired
  DeliveryZoneService zoneService;

  public boolean hasPermissionOnZone(Long userId, final long zoneId) {
    List<DeliveryZone> zones = zoneService.getByUserForRight(userId, MANAGE_DISTRIBUTION);

    return exists(zones, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        DeliveryZone zone = (DeliveryZone) o;
        return zone.getId() == zoneId;
      }
    });
  }
}
