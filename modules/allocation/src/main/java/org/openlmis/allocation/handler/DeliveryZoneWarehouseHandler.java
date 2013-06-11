/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.handler;


import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.openlmis.allocation.service.DeliveryZoneWarehouseService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deliveryZoneWarehouseHandler")
public class DeliveryZoneWarehouseHandler extends AbstractModelPersistenceHandler{

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
  protected String getDuplicateMessageKey() {
    return null;
  }
}
