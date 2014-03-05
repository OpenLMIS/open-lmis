/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.upload;


import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.openlmis.core.service.DeliveryZoneWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * DeliveryZoneWarehouseHandler is used for uploads of DeliveryZone and Warehouse mapping.
 * It uploads each DeliveryZoneWarehouse record by record.
 */
@Component
public class DeliveryZoneWarehouseHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private DeliveryZoneWarehouseService service;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    DeliveryZoneWarehouse deliveryZoneWarehouse = (DeliveryZoneWarehouse) record;
    return service.getByDeliveryZoneCodeAndWarehouseCode(deliveryZoneWarehouse.getDeliveryZone().getCode(), deliveryZoneWarehouse.getWarehouse().getCode());
  }

  @Override
  protected void save(BaseModel record) {
    service.save((DeliveryZoneWarehouse) record);
  }

  @Override
  public String getMessageKey() {
    return null;
  }
}
