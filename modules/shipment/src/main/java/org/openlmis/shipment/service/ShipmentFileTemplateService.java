/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;

import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.repository.ShipmentTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentFileTemplateService {


  @Autowired
  ShipmentTemplateRepository shipmentTemplateRepository;

  @Transactional
  public void update(ShipmentFileTemplate shipmentFileTemplate) {
    //TODO should ONLY update based on column.name
    shipmentTemplateRepository.updateShipmentConfiguration(shipmentFileTemplate.getShipmentConfiguration());
    shipmentTemplateRepository.deleteAllShipmentFileColumns();
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileTemplate.getShipmentFileColumns()) {
      shipmentTemplateRepository.insertShipmentFileColumn(shipmentFileColumn);
    }
  }

  public ShipmentFileTemplate get() {
    return new ShipmentFileTemplate(shipmentTemplateRepository.getShipmentConfiguration(),
      shipmentTemplateRepository.getAllShipmentFileColumns());
  }
}
