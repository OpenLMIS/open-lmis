/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.repository.ShipmentRepository;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;
  @InjectMocks
  private ShipmentService shipmentService;

  @Test
  public void shouldInsertShipment() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shipmentService.insertShippedLineItem(shippedLineItem);
    verify(shipmentRepository).insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }

}
