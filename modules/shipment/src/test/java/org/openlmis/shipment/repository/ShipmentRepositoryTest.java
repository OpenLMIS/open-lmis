/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentRepositoryTest {

  @Mock
  private ShipmentMapper shipmentMapper;
  @InjectMocks
  private ShipmentRepository shipmentRepository;

  @Test
  public void shouldInsertShipment() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shipmentRepository.insertShippedLineItem(shippedLineItem);
    verify(shipmentMapper).insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldInsertShipmentFileInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentRepository.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentMapper).insertShipmentFileInfo(shipmentFileInfo);
  }
}