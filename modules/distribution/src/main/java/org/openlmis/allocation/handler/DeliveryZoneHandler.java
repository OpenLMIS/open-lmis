/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.handler;

import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.service.DeliveryZoneService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.AbstractModelPersistenceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryZoneHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private DeliveryZoneService service;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return service.getByCode(((DeliveryZone) record).getCode());
  }

  @Override
  protected void save(BaseModel record) {
    service.save((DeliveryZone) record);
  }

  @Override
  protected String getDuplicateMessageKey() {
    return "error.duplicate.delivery.zone";
  }
}
