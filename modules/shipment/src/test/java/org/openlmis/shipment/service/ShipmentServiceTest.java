/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;


  @Mock
  private ProductService productService;

  @InjectMocks
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldInsertShipment() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));


    when(productService.getIdForCode("P10")).thenReturn(1l);

    shipmentService.insertShippedLineItem(shipmentLineItem);

    verify(productService).getIdForCode("P10");
    verify(shipmentRepository).insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfProductCodeIsNotValid() throws Exception {

    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    when(productService.getIdForCode("P10")).thenReturn(null);


    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfQuantityNegative() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, -1)));

    when(productService.getIdForCode("P10")).thenReturn(1l);
    exException.expect(DataException.class);
    exException.expectMessage("error.negative.shipped.quantity");

    shipmentService.insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }


  @Test
  public void shouldGetProcessedTimeStampByOrderId() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    shipmentLineItem.setOrderId(1L);
    Date expectedTimestamp = new Date();
    when(shipmentRepository.getProcessedTimeStamp(shipmentLineItem)).thenReturn(expectedTimestamp);

    Date processTimeStamp = shipmentService.getProcessedTimeStamp(shipmentLineItem);

    assertThat(processTimeStamp, is(expectedTimestamp));
    verify(shipmentRepository).getProcessedTimeStamp(shipmentLineItem);
  }
}
