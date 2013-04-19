/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.rules.ExpectedException.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentRepositoryTest {

  @Rule
  public ExpectedException expectedException = none();

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

  @Test
  public void shouldThrowExceptionIfShipmentFileHasIncorrectOrderNumber() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setOrderId(1);
    doThrow(new DataIntegrityViolationException("violates foreign key constraint \"shipped_line_items_orderid_fkey\"")).when(shipmentMapper).insertShippedLineItem(shippedLineItem);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Unknown order number");

    shipmentRepository.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldThrowExceptionIfShipmentFileHasIncorrectProductCode() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setProductCode("R10");
    doThrow(new DataIntegrityViolationException("violates foreign key constraint \"shipped_line_items_productcode_fkey\"")).when(shipmentMapper).insertShippedLineItem(shippedLineItem);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Unknown product code");

    shipmentRepository.insertShippedLineItem(shippedLineItem);
    }

//  @Test
//  public void shouldThrowExceptionWhenDuplicateOrderNumberIsEncounteredInShipmentFile() throws Exception {
//      ShippedLineItem shippedLineItem = new ShippedLineItem();
//      shippedLineItem.setOrderId(1);
//
//    expectedException.expect(DataException.class);
//    expectedException.expectMessage("Order Number already processed");
//
//    shipmentRepository.insertShippedLineItem(shippedLineItem);
//
//
//  }
}