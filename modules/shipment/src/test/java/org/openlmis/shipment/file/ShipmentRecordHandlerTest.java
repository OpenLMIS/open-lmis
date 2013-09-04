/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.service.ShipmentService;
import org.openlmis.upload.model.AuditFields;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentRecordHandlerTest {
  @Mock
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  ;

  @InjectMocks
  private ShipmentRecordHandler shipmentRecordHandler;

  @Test
  public void shouldInsert() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    Date currentTimestamp = new Date();
    when(shipmentService.getProcessedTimeStamp(shipmentLineItem)).thenReturn(null);

    shipmentRecordHandler.execute(shipmentLineItem, 1, new AuditFields(currentTimestamp));

    assertThat(shipmentLineItem.getModifiedDate(), is(currentTimestamp));
    verify(shipmentService).insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldThrowExceptionIfOrderIdIsAlreadyProcessed() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    Date date = new Date(calendar.getTimeInMillis());

    AuditFields auditFields = new AuditFields();
    auditFields.setCurrentTimestamp(new Date());

    when(shipmentService.getProcessedTimeStamp(shipmentLineItem)).thenReturn(date);

    expectedException.expect(dataExceptionMatcher("error.duplicate.order"));

    shipmentRecordHandler.execute(shipmentLineItem, 1, auditFields);
  }

  @Test
  public void shouldUpdateShippedLineItemIfOrderIdPresentWithSameTimeStamp() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(currentTimestamp);

    Long shippedLineItemFromDbId = 1L;
    ShipmentLineItem shipmentLineItemFromDB = new ShipmentLineItem();
    shipmentLineItemFromDB.setModifiedDate(currentTimestamp);
    shipmentLineItemFromDB.setId(shippedLineItemFromDbId);

    when(shipmentService.getShippedLineItem(shipmentLineItem)).thenReturn(shipmentLineItemFromDB);

    shipmentRecordHandler.execute(shipmentLineItem, 1, auditFields);

    assertThat(shipmentLineItem.getModifiedDate(), is(currentTimestamp));
    assertThat(shipmentLineItem.getId(), is(shippedLineItemFromDbId));
    verify(shipmentService).updateShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldThrowErrorIfOrderAlreadyProcessedButProductCodeIsNew() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    Date processedDate = new Date(calendar.getTimeInMillis());

    AuditFields auditFields = new AuditFields();
    auditFields.setCurrentTimestamp(new Date());

    when(shipmentService.getProcessedTimeStamp(shipmentLineItem)).thenReturn(processedDate);

    expectedException.expect(dataExceptionMatcher("error.duplicate.order"));

    shipmentRecordHandler.execute(shipmentLineItem, 1, auditFields);
  }
}
