/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.openlmis.core.repository.DeliveryZoneWarehouseRepository;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryZoneWarehouseService {

  @Autowired
  private DeliveryZoneWarehouseRepository repository;

  @Autowired
  FacilityService facilityService;

  @Autowired
  DeliveryZoneService deliveryZoneService;

  public void save(DeliveryZoneWarehouse deliveryZoneWarehouse) {
    fillFacility(deliveryZoneWarehouse);
    fillDeliveryZone(deliveryZoneWarehouse);
    if (deliveryZoneWarehouse.getId() == null)
      repository.insert(deliveryZoneWarehouse);
    else
      repository.update(deliveryZoneWarehouse);
  }

  private void fillDeliveryZone(DeliveryZoneWarehouse deliveryZoneWarehouse) {
    DeliveryZone zone = deliveryZoneService.getByCode(deliveryZoneWarehouse.getDeliveryZone().getCode());
    if (zone == null) throw new DataException("deliveryZone.code.invalid");
    deliveryZoneWarehouse.setDeliveryZone(zone);
  }

  private void fillFacility(DeliveryZoneWarehouse deliveryZoneWarehouse) {
    Facility facility = facilityService.getByCode(deliveryZoneWarehouse.getWarehouse());
    if (facility == null) throw new DataException("warehouse.code.invalid");
    deliveryZoneWarehouse.setWarehouse(facility);
  }

  public DeliveryZoneWarehouse getByDeliveryZoneCodeAndWarehouseCode(String deliveryZoneCode, String warehouseCode) {
    return repository.getByDeliveryZoneCodeAndWarehouseCode(deliveryZoneCode, warehouseCode);
  }
}
