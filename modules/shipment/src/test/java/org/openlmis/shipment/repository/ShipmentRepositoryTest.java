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

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

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
  public void shouldInsertShipmentIfNewLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    shipmentRepository.save(shipmentLineItem);

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

    shipmentRepository.save(shipmentLineItem);
  }
}