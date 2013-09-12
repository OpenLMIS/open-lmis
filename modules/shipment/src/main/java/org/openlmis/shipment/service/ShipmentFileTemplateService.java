/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;

import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.repository.ShipmentTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShipmentFileTemplateService {


  @Autowired
  ShipmentTemplateRepository shipmentTemplateRepository;

  @Transactional
  public void update(ShipmentFileTemplate shipmentFileTemplate) {
    shipmentTemplateRepository.updateShipmentConfiguration(shipmentFileTemplate.getShipmentConfiguration());

    for (ShipmentFileColumn shipmentFileColumn : shipmentFileTemplate.getShipmentFileColumns()) {
      shipmentTemplateRepository.update(shipmentFileColumn);
    }
  }

  public ShipmentFileTemplate get() {
    ShipmentConfiguration config = shipmentTemplateRepository.getShipmentConfiguration();
    List<ShipmentFileColumn> columns = shipmentTemplateRepository.getAllShipmentFileColumns();

    return new ShipmentFileTemplate(config, columns);
  }
}
