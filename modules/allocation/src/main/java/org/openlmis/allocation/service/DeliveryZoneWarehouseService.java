/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.service;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.openlmis.allocation.repository.DeliveryZoneWarehouseRepository;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryZoneWarehouseService {

  private static final String WAREHOUSE_CODE_INVALID = "warehouse.code.invalid";
  public static final String DELIVERY_ZONE_CODE_INVALID = "deliveryZone.code.invalid";

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
    if(zone == null) throw new DataException(DELIVERY_ZONE_CODE_INVALID);
    deliveryZoneWarehouse.setDeliveryZone(zone);
  }

  private void fillFacility(DeliveryZoneWarehouse deliveryZoneWarehouse) {
    Facility facility = facilityService.getByCode(deliveryZoneWarehouse.getWarehouse());
    if(facility == null) throw new DataException(WAREHOUSE_CODE_INVALID);
    deliveryZoneWarehouse.setWarehouse(facility);
  }

  public DeliveryZoneWarehouse getByDeliveryZoneCodeAndWarehouseCode(String deliveryZoneCode, String warehouseCode) {
    return repository.getByDeliveryZoneCodeAndWarehouseCode(deliveryZoneCode, warehouseCode);
  }
}
