/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.service;

import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.shipment.repository.ShipmentTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Exposes the services for handling shipment file template and its configuration.
 */

@Service
public class ShipmentFileTemplateService {


  @Autowired
  ShipmentTemplateRepository shipmentTemplateRepository;

  @Transactional
  public void update(EDIFileTemplate shipmentFileTemplate) {
    shipmentTemplateRepository.updateShipmentConfiguration(shipmentFileTemplate.getConfiguration());

    for (EDIFileColumn shipmentFileColumn : shipmentFileTemplate.getColumns()) {
      shipmentTemplateRepository.update(shipmentFileColumn);
    }
  }

  public EDIFileTemplate get() {
    EDIConfiguration config = shipmentTemplateRepository.getShipmentConfiguration();
    List<EDIFileColumn> columns = shipmentTemplateRepository.getAllShipmentFileColumns();

    return new EDIFileTemplate(config, columns);
  }
}
