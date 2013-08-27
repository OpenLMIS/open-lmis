/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.springframework.beans.factory.annotation.Autowired;

public class ShipmentFileTemplateController {

  @Autowired
  ShipmentFileTemplateService service;

  public ShipmentFileTemplate get() {
    return service.get();
  }

  public void update(ShipmentFileTemplate shipmentFileTemplate) {
    service.update(shipmentFileTemplate);
  }
}
