/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.distribution.domain.Distribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class is responsible for checking if the user has the given right on a delivery zone.
 */

@Service
public class DistributionPermissionService {

  @Autowired
  private DistributionService distributionService;

  @Autowired
  private DeliveryZoneService deliveryZoneService;

  public Boolean hasPermission(Long userId, String permission, final Distribution distribution) {
    return hasPermissionForDeliveryZone(userId, permission, distribution);
  }

  public Boolean hasPermission(Long userId, String permission, final Long distributionId) {
    final Distribution distribution = distributionService.getBy(distributionId);
    return hasPermissionForDeliveryZone(userId, permission, distribution);
  }

  private Boolean hasPermissionForDeliveryZone(Long userId, String permission, final Distribution distribution) {
    List<DeliveryZone> deliveryZones = deliveryZoneService.getByUserForRight(userId, permission);
    boolean deliveryZoneExists = CollectionUtils.exists(deliveryZones, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((DeliveryZone) o).getCode().equals(distribution.getDeliveryZone().getCode());
      }
    });
    return deliveryZoneExists;
  }

}
