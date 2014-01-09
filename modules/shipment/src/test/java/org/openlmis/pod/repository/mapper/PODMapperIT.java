/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.pod.repository.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.context.ApplicationTestContext;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

@Category(UnitTests.class)
public class PODMapperIT extends ApplicationTestContext {

  @Autowired
  PODMapper podMapper;

  @Autowired
  QueryExecutor queryExecutor;

  String productCode;
  Order order;

  @Before
  public void setUp() throws Exception {
    productCode = "P10";
    order = insertOrder(productCode);
  }

  @Test
  public void shouldInsertPOD() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    Rnr rnr = order.getRnr();
    orderPod.fillPOD(rnr);
    podMapper.insertPOD(orderPod);

    OrderPOD savedOrderPod = podMapper.getPODByOrderId(orderPod.getOrderId());

    assertThat(savedOrderPod.getId(), is(notNullValue()));
    assertThat(savedOrderPod.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(savedOrderPod.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(savedOrderPod.getPeriodId(), is(rnr.getPeriod().getId()));
  }

  @Test
  public void shouldInsertPODLineItem() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    podMapper.insertPOD(orderPod);

    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, 100);
    orderPodLineItem.setQuantityShipped(100);
    orderPodLineItem.setDispensingUnit("Tablets");
    orderPodLineItem.setPacksToShip(10);
    podMapper.insertPODLineItem(orderPodLineItem);

    List<OrderPODLineItem> orderPodLineItems = podMapper.getPODLineItemsByPODId(orderPod.getId());
    assertThat(orderPodLineItems.size(), is(1));
    assertThat(orderPodLineItems.get(0).getProductCode(), is(productCode));
    assertThat(orderPodLineItems.get(0).getQuantityShipped(), is(100));
    assertThat(orderPodLineItems.get(0).getDispensingUnit(), is("Tablets"));
    assertThat(orderPodLineItems.get(0).getPacksToShip(), is(10));
    assertThat(orderPodLineItems.get(0).getProductName(), is(nullValue()));
  }

  @Test
  public void shouldGetPodLineItemsByOrderId() throws SQLException {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    podMapper.insertPOD(orderPod);
    queryExecutor.executeUpdate("INSERT INTO pod_line_items (podId, productCode, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?)",
      orderPod.getId(), productCode, 100, 1, 1);

    List<OrderPODLineItem> orderPodLineItems = podMapper.getPODLineItemsByPODId(orderPod.getId());
    assertThat(orderPodLineItems.size(), is(1));
    assertThat(orderPodLineItems.get(0).getProductCode(), is(productCode));
  }

  @Test
  public void shouldGetPODByOrderId() throws SQLException {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    queryExecutor.executeUpdate("INSERT INTO pod(orderId) values(?)", order.getId());

    OrderPOD savedOrderPOD = podMapper.getPODByOrderId(order.getId());
    assertThat(savedOrderPOD, is(notNullValue()));
    assertThat(savedOrderPOD.getOrderId(), is(order.getId()));
  }

  @Test
  public void shouldGetNPreviousPODLineItemsAfterGivenTrackingDateForGivenProgramPeriodAndProduct() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.fillPOD(order.getRnr());
    Rnr requisition = order.getRnr();
    podMapper.insertPOD(orderPod);
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, 100);
    podMapper.insertPODLineItem(orderPodLineItem);

    List<OrderPODLineItem> nOrderPodLineItems = podMapper.getNPodLineItems(productCode, requisition, 1, DateTime.now().minusDays(5).toDate());

    assertThat(nOrderPodLineItems, hasItems(orderPodLineItem));
  }
}
