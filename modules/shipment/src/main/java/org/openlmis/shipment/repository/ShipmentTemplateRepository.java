/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository;

import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.shipment.repository.mapper.ShipmentConfigurationMapper;
import org.openlmis.shipment.repository.mapper.ShipmentFileColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for shipment file template and its configuration related database operations.
 */

@Repository
public class ShipmentTemplateRepository {

  @Autowired
  private ShipmentFileColumnMapper shipmentFileColumnMapper;

  @Autowired
  ShipmentConfigurationMapper shipmentConfigurationMapper;

  public List<EDIFileColumn> getAllShipmentFileColumns() {
    return shipmentFileColumnMapper.getAll();
  }

  public EDIConfiguration getShipmentConfiguration() {
    return shipmentConfigurationMapper.get();
  }

  public void updateShipmentConfiguration(EDIConfiguration shipmentConfiguration) {
    shipmentConfigurationMapper.update(shipmentConfiguration);
  }

  public void update(EDIFileColumn shipmentFileColumn) {
    shipmentFileColumnMapper.update(shipmentFileColumn);
  }
}
