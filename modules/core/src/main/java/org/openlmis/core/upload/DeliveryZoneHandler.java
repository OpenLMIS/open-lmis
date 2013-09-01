/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.service.DeliveryZoneService;
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
  public String getMessageKey() {
    return "error.duplicate.delivery.zone";
  }
}
