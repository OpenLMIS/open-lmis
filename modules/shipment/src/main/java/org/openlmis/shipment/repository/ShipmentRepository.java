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

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ShipmentRepository {

  private ShipmentMapper shipmentMapper;

  @Autowired
  public ShipmentRepository(ShipmentMapper shipmentMapper) {
    this.shipmentMapper = shipmentMapper;
  }

  public void insertShippedLineItem(ShipmentLineItem shipmentLineItem) {
    try {
      shipmentMapper.insertShippedLineItem(shipmentLineItem);
    } catch (DataIntegrityViolationException exception) {
      throw new DataException("error.incorrect.length");
    }
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);
  }

  public ShipmentLineItem getShippedLineItem(ShipmentLineItem shipmentLineItem) {
    return shipmentMapper.getShippedLineItem(shipmentLineItem);
  }

  public void updateShippedLineItem(ShipmentLineItem shipmentLineItem) {
    shipmentMapper.updateShippedLineItem(shipmentLineItem);
  }
}
