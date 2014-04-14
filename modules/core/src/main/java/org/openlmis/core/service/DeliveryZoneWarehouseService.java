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

import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.DeliveryZoneWarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Exposes the services for handling DeliveryZoneWarehouse entity.
 */

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
