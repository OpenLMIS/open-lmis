/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
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
    shippedLineItem.setRnrId(1L);
    doThrow(new DataIntegrityViolationException("violates foreign key constraint \"shipped_line_items_rnrid_fkey\"")).when(shipmentMapper).insertShippedLineItem(shippedLineItem);

    expectedException.expect(dataExceptionMatcher("error.unknown.order"));

    shipmentRepository.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldThrowExceptionIfShipmentFileHasIncorrectProductCode() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setProductCode("R10");
    doThrow(new DataIntegrityViolationException("violates foreign key constraint \"shipped_line_items_productcode_fkey\"")).when(shipmentMapper).insertShippedLineItem(shippedLineItem);

    expectedException.expect(dataExceptionMatcher("error.unknown.product"));

    shipmentRepository.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldThrowExceptionIfShipmentFileHasIncorrectDataLength() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setProductCode("R10");
    doThrow(new DataIntegrityViolationException("Incorrect data length")).when(shipmentMapper).insertShippedLineItem(shippedLineItem);

    expectedException.expect(dataExceptionMatcher("error.incorrect.length"));

    shipmentRepository.insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldUpdateShippedLineItem() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shipmentRepository.updateShippedLineItem(shippedLineItem);
    verify(shipmentMapper).updateShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldGetProcessedTimeStampByOrderId() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setRnrId(1L);
    Date expectedTimestamp = new Date();
    when(shipmentMapper.getProcessedTimeStamp(shippedLineItem)).thenReturn(expectedTimestamp);

    Date processTimeStamp = shipmentRepository.getProcessedTimeStamp(shippedLineItem);

    assertThat(processTimeStamp, is(expectedTimestamp));
    verify(shipmentMapper).getProcessedTimeStamp(shippedLineItem);
  }
}