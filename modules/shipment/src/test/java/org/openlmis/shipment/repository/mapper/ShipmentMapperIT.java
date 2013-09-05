/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRnr;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ShipmentMapperIT {

  @Autowired
  ShipmentMapper mapper;
  @Autowired
  ProductMapper productMapper;
  @Autowired
  OrderMapper orderMapper;
  @Autowired
  RequisitionMapper requisitionMapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  private ProgramMapper programMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private QueryExecutor queryExecutor;


  @Test
  public void shouldInsertShippedLineItems() throws Exception {

    ShipmentLineItem shipmentLineItem = createShippedLineItem();
    mapper.insertShippedLineItem(shipmentLineItem);

    assertThat(shipmentLineItem.getId(), is(notNullValue()));

    String fetchShipmentFileInfoQuery = "Select * from shipment_line_items where id = ?";
    ResultSet shipmentFileInfoResultSet = queryExecutor.execute(fetchShipmentFileInfoQuery, Arrays.asList(shipmentLineItem.getId()));
    shipmentFileInfoResultSet.next();
    assertThat(shipmentFileInfoResultSet.getLong("rnrId"), is(shipmentLineItem.getRnrId()));
    assertThat(shipmentFileInfoResultSet.getString("productCode"), is(shipmentLineItem.getProductCode()));
    assertThat(shipmentFileInfoResultSet.getInt("quantityShipped"), is(shipmentLineItem.getQuantityShipped()));
  }

  private ShipmentLineItem createShippedLineItem() {
    Product product = make(a(defaultProduct));
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod period = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod, with(ProcessingPeriodBuilder.scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(period);
    Program program = make(a(defaultProgram));
    programMapper.insert(program);
    Rnr requisition = make(a(defaultRnr, with(RequisitionBuilder.facility, facility), with(RequisitionBuilder.periodId, period.getId())));
    requisitionMapper.insert(requisition);

    productMapper.insert(product);

    return make(a(defaultShipmentLineItem,
      with(productCode, product.getCode()),
      with(rnrId, requisition.getId()),
      with(quantityShipped, 23),
      with(shippedDate, new Date()),
      with(packedDate, new Date())));

  }

  @Test
  public void shouldInsertShipmentFileInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setFileName("abc");

    mapper.insertShipmentFileInfo(shipmentFileInfo);

    assertThat(shipmentFileInfo.getId(), is(notNullValue()));

  }

  @Test
  public void shouldGetShipmentFileInfoById() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setFileName("abc");

    mapper.insertShipmentFileInfo(shipmentFileInfo);

    ShipmentFileInfo result = mapper.getShipmentFileInfo(shipmentFileInfo.getId());

    assertThat(result.getId(), is(shipmentFileInfo.getId()));
  }

  @Test
  public void shouldSelectShippedLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = createShippedLineItem();

    mapper.insertShippedLineItem(shipmentLineItem);

    ShipmentLineItem returnedShipmentLineItem = mapper.getShippedLineItem(shipmentLineItem);

    assertThat(returnedShipmentLineItem, is(shipmentLineItem));
  }

  @Test
  public void shouldUpdateShippedLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = createShippedLineItem();
    mapper.insertShippedLineItem(shipmentLineItem);

    shipmentLineItem.setQuantityShipped(10);

    mapper.updateShippedLineItem(shipmentLineItem);

    ShipmentLineItem shipmentLineItemFromDB = mapper.getShippedLineItem(shipmentLineItem);

    assertThat(shipmentLineItemFromDB.getQuantityShipped(), is(10));
  }

  @Test
  public void shouldGetProcessedTimestampForShippedLineItem() throws Exception {
    ShipmentLineItem shipmentLineItem = createShippedLineItem();
    mapper.insertShippedLineItem(shipmentLineItem);

    ShipmentLineItem shipmentLineItemFromDB = mapper.getShippedLineItem(shipmentLineItem);

    Date date = mapper.getProcessedTimeStamp(shipmentLineItem);

    assertThat(date, is(shipmentLineItemFromDB.getModifiedDate()));
  }


}
