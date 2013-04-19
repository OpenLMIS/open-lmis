/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.service.ShipmentService;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentRecordHandlerTest {
  @Mock
  private ShipmentService shipmentService;
  @InjectMocks
  private ShipmentRecordHandler shipmentRecordHandler;

  @Test
  public void shouldInsert() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shipmentRecordHandler.execute(shippedLineItem, 1, null);
    verify(shipmentService).insertShippedLineItem(shippedLineItem);
  }
}
