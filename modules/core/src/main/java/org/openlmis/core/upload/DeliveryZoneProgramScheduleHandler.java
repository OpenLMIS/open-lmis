/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.upload;

import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryZoneProgramScheduleHandler extends AbstractModelPersistenceHandler {

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
    return "error.duplicate.delivery.zone.program";
  }
}
