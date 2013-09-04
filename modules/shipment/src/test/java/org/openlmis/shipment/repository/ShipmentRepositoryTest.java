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
import org.openlmis.shipment.domain.ShipmentLineItem;
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
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    shipmentRepository.insertShippedLineItem(shipmentLineItem);
    verify(shipmentMapper).insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldInsertShipmentFileInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentRepository.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentMapper).insertShipmentFileInfo(shipmentFileInfo);
  }


  @Test
  public void shouldThrowExceptionIfShipmentFileHasIncorrectDataLength() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    shipmentLineItem.setProductCode("R10");
    doThrow(new DataIntegrityViolationException("Incorrect data length")).when(shipmentMapper).insertShippedLineItem(shipmentLineItem);

    expectedException.expect(dataExceptionMatcher("error.incorrect.length"));

    shipmentRepository.insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldUpdateShippedLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    shipmentRepository.updateShippedLineItem(shipmentLineItem);
    verify(shipmentMapper).updateShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldGetProcessedTimeStampByOrderId() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    shipmentLineItem.setRnrId(1L);
    Date expectedTimestamp = new Date();
    when(shipmentMapper.getProcessedTimeStamp(shipmentLineItem)).thenReturn(expectedTimestamp);

    Date processTimeStamp = shipmentRepository.getProcessedTimeStamp(shipmentLineItem);

    assertThat(processTimeStamp, is(expectedTimestamp));
    verify(shipmentMapper).getProcessedTimeStamp(shipmentLineItem);
  }
}