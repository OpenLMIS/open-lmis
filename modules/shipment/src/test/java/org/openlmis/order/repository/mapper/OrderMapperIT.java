/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.repository.mapper.RequisitionMapper;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.order.domain.OrderStatus.PACKED;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class OrderMapperIT {

  @Autowired
  private OrderMapper mapper;
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;
  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;
  @Autowired
  private RequisitionMapper requisitionMapper;
  @Autowired
  private ShipmentMapper shipmentMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  private ProcessingSchedule processingSchedule;
  private Facility facility;
  private ProcessingPeriod processingPeriod;

  @Before
  public void setUp() throws Exception {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);
    processingPeriod = insertPeriod();
  }

  @Test
  public void shouldInsertOrder() throws Exception {
    Rnr rnr = insertRequisition(1L);
    Order order = new Order(rnr);
    mapper.insert(order);
    List<Long> orderIds = new ArrayList();
    orderIds.add(order.getId());
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM orders WHERE id = ?", orderIds);
    resultSet.next();
    assertThat(resultSet.getLong("id"), is(order.getId()));
  }

  @Test
  public void shouldGetAllOrders() throws Exception {
    Order order1 = insertOrder(3L);
    Order order2 = insertOrder(1L);

    Date today = DateTime.now().toDate();
    Date oneYearBack = DateTime.now().minusYears(1).toDate();

    updateOrderCreatedTime(order1, oneYearBack);
    updateOrderCreatedTime(order2, today);

    List<Order> orders = mapper.getAll();
    assertThat(orders.size(), is(2));
    assertThat(orders.get(1).getId(), is(order1.getId()));
    assertThat(orders.get(1).getRnr().getId(), is(order1.getRnr().getId()));
    assertThat(orders.get(1).getShipmentFileInfo(), is(nullValue()));
    assertThat(orders.get(0).getId(), is(order2.getId()));
  }

  @Test
  public void shouldGetShipmentFileInfoWhileFetchingOrders() throws Exception {
    Order order1 = insertOrder(3L);
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setFileName("abc.csv");
    shipmentFileInfo.setProcessingError(false);
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);

    order1.updateShipmentFileInfo(shipmentFileInfo);
    mapper.updateShipmentInfo(order1);

    List<Order> orders = mapper.getAll();
    assertThat(orders.get(0).getShipmentFileInfo().getFileName(), is("abc.csv"));
    assertThat(orders.get(0).getShipmentFileInfo().isProcessingError(), is(false));
  }

  @Test
  public void shouldUpdateStatusAndShipmentIdForOrder() throws Exception {
    Rnr rnr = insertRequisition(1L);
    Order order = new Order(rnr);
    mapper.insert(order);
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setFileName("ord_1.csv");
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);

    order.updateShipmentFileInfo(shipmentFileInfo);

    mapper.updateShipmentInfo(order);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM orders WHERE rnrid=?", Arrays.asList(order.getRnr().getId()));

    resultSet.next();

    assertThat(resultSet.getString("status"), is(PACKED.name()));
    assertThat(resultSet.getLong("shipmentId"), is(shipmentFileInfo.getId()));
  }

  @Test
  public void shouldGetOrderById() {
    Order expectedOrder = insertOrder(1L);

    Order savedOrder = mapper.getById(expectedOrder.getId());
    assertThat(savedOrder.getId(), is(expectedOrder.getId()));
    assertThat(savedOrder.getRnr().getId(), is(expectedOrder.getRnr().getId()));
  }

  private int updateOrderCreatedTime(Order order, Date date) throws SQLException {
    List paramList = new ArrayList();
    paramList.add(new java.sql.Date(date.getTime()));
    paramList.add(order.getId());
    return queryExecutor.executeUpdate("UPDATE orders SET createdDate = ? WHERE id = ?", paramList);
  }

  private Order insertOrder(Long programId) {
    Rnr rnr = insertRequisition(programId);
    Order order = new Order(rnr);
    mapper.insert(order);
    return order;
  }

  private Rnr insertRequisition(Long programId) {
    Rnr rnr = make(a(defaultRnr, with(RequisitionBuilder.facility, facility),
        with(periodId, processingPeriod.getId()), with(program, new Program(programId))));
    requisitionMapper.insert(rnr);
    return rnr;
  }

  private ProcessingPeriod insertPeriod() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(processingPeriod);
    return processingPeriod;
  }
}
