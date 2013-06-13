/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.handler;

import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.openlmis.allocation.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryZoneProgramScheduleHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_DELIVERY_ZONE_CODE_AND_PROGRAM_CODE_COMBINATION_ERROR = "error.deliveryZoneProgram.duplicate";
  @Autowired
  private DeliveryZoneProgramScheduleService service;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    DeliveryZoneProgramSchedule deliveryZoneProgramSchedule = (DeliveryZoneProgramSchedule) record;
    return service.getByDeliveryZoneCodeAndProgramCode(deliveryZoneProgramSchedule.getDeliveryZone().getCode(),
        deliveryZoneProgramSchedule.getProgram().getCode());
  }

  @Override
  protected void save(BaseModel record) {
    service.save((DeliveryZoneProgramSchedule) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return DUPLICATE_DELIVERY_ZONE_CODE_AND_PROGRAM_CODE_COMBINATION_ERROR;
  }
}
