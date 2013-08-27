/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.repository;

import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.repository.mapper.ShipmentConfigurationMapper;
import org.openlmis.shipment.repository.mapper.ShipmentFileColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShipmentTemplateRepository {

  @Autowired
  private ShipmentFileColumnMapper shipmentFileColumnMapper;

  @Autowired
  ShipmentConfigurationMapper shipmentConfigurationMapper;

  public List<ShipmentFileColumn> getAllShipmentFileColumns() {
    return shipmentFileColumnMapper.getAll();
  }

  public ShipmentConfiguration getShipmentConfiguration() {
    return shipmentConfigurationMapper.get();
  }

  public void updateShipmentConfiguration(ShipmentConfiguration shipmentConfiguration) {
    shipmentConfigurationMapper.update(shipmentConfiguration);
  }

  public void deleteAllShipmentFileColumns() {
    shipmentFileColumnMapper.deleteAll();
  }

  public void insertShipmentFileColumn(ShipmentFileColumn shipmentFileColumn) {
    shipmentFileColumnMapper.insert(shipmentFileColumn);
  }
}
