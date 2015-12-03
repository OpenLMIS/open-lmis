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
import org.junit.runner.RunWith;
import org.openlmis.context.ShipmentTestContext;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class PODMapperIT extends ShipmentTestContext {

  @Autowired
  PODMapper mapper;

  @Autowired
  QueryExecutor queryExecutor;

  private String productCode;
  private Order order;
  private String dispensingUnit;
  private String productCategory;
  private String productName;
  private Integer productCategoryDisplayOrder;
  private Integer productDisplayOrder;

  @Before
  public void setUp() throws Exception {
    productCode = "P10";
    dispensingUnit = "Tablets";
    productCategory = "productCategory";
    productName = "productName";
    order = insertOrder(productCode);
    productCategoryDisplayOrder = 10;
    productDisplayOrder = 10;
  }

  @Test
  public void shouldInsertPOD() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.setOrderNumber("OYELL_FVR00000001R");
    orderPod.setDeliveredBy("deliveredBy");
    orderPod.setReceivedBy("acceptedBy");

    Date receivedDate = new Date();
    orderPod.setReceivedDate(receivedDate);
    Rnr rnr = order.getRnr();
    orderPod.fillPOD(rnr);
    mapper.insertPOD(orderPod);

    OrderPOD savedOrderPod = mapper.getPODByOrderId(orderPod.getOrderId());

    assertThat(savedOrderPod.getId(), is(notNullValue()));
    assertThat(savedOrderPod.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(savedOrderPod.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(savedOrderPod.getPeriodId(), is(rnr.getPeriod().getId()));
    assertThat(savedOrderPod.getDeliveredBy(), is("deliveredBy"));
    assertThat(savedOrderPod.getReceivedBy(), is("acceptedBy"));
    assertThat(savedOrderPod.getReceivedDate(), is(receivedDate));
    assertThat(savedOrderPod.getOrderNumber(), is("OYELL_FVR00000001R"));
  }

  @Test
  public void shouldInsertPODLineItem() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.setOrderNumber("OYELL_FVR00000001R");
    orderPod.setFacilityId(order.getRnr().getFacility().getId());
    orderPod.setPeriodId(order.getRnr().getPeriod().getId());
    orderPod.setProgramId(1L);
    mapper.insertPOD(orderPod);

    Integer quantityShipped = 1000;
    Integer quantityReturned = 55;
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, productCategory,
      productCategoryDisplayOrder, productDisplayOrder, 100, productName, dispensingUnit, 10, quantityShipped, quantityReturned, true, "P98",
      "notes");
    mapper.insertPODLineItem(orderPodLineItem);

    List<OrderPODLineItem> orderPodLineItems = mapper.getPODLineItemsByPODId(orderPod.getId());
    assertThat(orderPodLineItems.size(), is(1));
    assertThat(orderPodLineItems.get(0).getProductCode(), is(productCode));
    assertThat(orderPodLineItems.get(0).getReplacedProductCode(), is("P98"));
    assertThat(orderPodLineItems.get(0).getDispensingUnit(), is(dispensingUnit));
    assertThat(orderPodLineItems.get(0).getPacksToShip(), is(10));
    assertThat(orderPodLineItems.get(0).getProductName(), is(productName));
    assertThat(orderPodLineItems.get(0).getProductCategory(), is(productCategory));
    assertThat(orderPodLineItems.get(0).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder));
    assertThat(orderPodLineItems.get(0).getProductDisplayOrder(), is(productDisplayOrder));
    assertThat(orderPodLineItems.get(0).getQuantityShipped(), is(quantityShipped));
    assertThat(orderPodLineItems.get(0).getQuantityReturned(), is(quantityReturned));
  }

  @Test
  public void shouldGetPodLineItemsByOrderId() throws SQLException {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.setOrderNumber("OYELL_FVR00000001R");
    orderPod.setFacilityId(order.getRnr().getFacility().getId());
    orderPod.setPeriodId(order.getRnr().getPeriod().getId());
    orderPod.setProgramId(1L);
    mapper.insertPOD(orderPod);
    String productCode1 = "productCode 1";
    String productCode2 = "ProductCode 2";
    String productCode3 = "productCode 3";
    insertProduct(productCode1);
    insertProduct(productCode2);
    insertProduct(productCode3);
    String productCategory = "product Category";
    Integer productCategoryDisplayOrder1 = 1;
    Integer productDisplayOrder1 = 1;
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode, this.productCategory, this.productCategoryDisplayOrder, productDisplayOrder, 100, 1, 1);
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode1, productCategory, productCategoryDisplayOrder1, productDisplayOrder1, 100, 1, 1);
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode3, productCategory, productCategoryDisplayOrder1, productDisplayOrder, 100, 1, 1);
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode2, productCategory, productCategoryDisplayOrder1, productDisplayOrder1, 100, 1, 1);

    List<OrderPODLineItem> orderPodLineItems = mapper.getPODLineItemsByPODId(orderPod.getId());

    assertThat(orderPodLineItems.size(), is(4));
    assertThat(orderPodLineItems.get(0).getProductCode(), is(productCode1));
    assertThat(orderPodLineItems.get(0).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder1));
    assertThat(orderPodLineItems.get(0).getProductDisplayOrder(), is(productDisplayOrder1));
    assertThat(orderPodLineItems.get(1).getProductCode(), is(productCode2));
    assertThat(orderPodLineItems.get(1).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder1));
    assertThat(orderPodLineItems.get(1).getProductDisplayOrder(), is(productDisplayOrder1));
    assertThat(orderPodLineItems.get(2).getProductCode(), is(productCode3));
    assertThat(orderPodLineItems.get(2).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder1));
    assertThat(orderPodLineItems.get(2).getProductDisplayOrder(), is(productDisplayOrder));
    assertThat(orderPodLineItems.get(3).getProductCode(), is(productCode));
    assertThat(orderPodLineItems.get(3).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder));
  }

  @Test
  public void shouldGetPODByOrderId() throws SQLException {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    queryExecutor.executeUpdate("INSERT INTO pod(orderId, orderNumber, " +
      "facilityId, programId, periodId) values(?,?,?,?,?)", order.getId(), "OYELL_FVR00000001R",  order.getRnr().getFacility().getId(),
      1L, order.getRnr().getPeriod().getId());

    OrderPOD savedOrderPOD = mapper.getPODByOrderId(order.getId());
    assertThat(savedOrderPOD, is(notNullValue()));
    assertThat(savedOrderPOD.getOrderId(), is(order.getId()));
  }

  @Test
  public void shouldGetNPreviousPODLineItemsAfterGivenTrackingDateForGivenProgramPeriodAndProduct() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.setOrderNumber("OYELL_FVR00000001R");
    orderPod.fillPOD(order.getRnr());
    Rnr requisition = order.getRnr();
    mapper.insertPOD(orderPod);
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, 100);
    mapper.insertPODLineItem(orderPodLineItem);

    List<OrderPODLineItem> nOrderPodLineItems = mapper.getNPodLineItems(productCode, requisition, 1,
      DateTime.now().minusDays(5).toDate());

    assertThat(nOrderPodLineItems, hasItems(orderPodLineItem));
  }

  @Test
  public void shouldGetPODWithLineItemsByPODId() throws Exception {
    OrderPOD expectedOrderPod = new OrderPOD();
    expectedOrderPod.setOrderId(order.getId());
    expectedOrderPod.setOrderNumber("OYELL_FVR00000001R");
    expectedOrderPod.setFacilityId(order.getRnr().getFacility().getId());
    expectedOrderPod.setPeriodId(order.getRnr().getPeriod().getId());
    expectedOrderPod.setProgramId(1L);
    mapper.insertPOD(expectedOrderPod);

    OrderPODLineItem lineItem1 = new OrderPODLineItem(expectedOrderPod.getId(), productCode, productCategory,
      productCategoryDisplayOrder, productDisplayOrder, 100, productName, dispensingUnit, 10, null, null, true, null, null);
    mapper.insertPODLineItem(lineItem1);

    OrderPOD orderPOD = mapper.getPODById(expectedOrderPod.getId());

    assertThat(orderPOD.getPodLineItems().size(), is(1));
    assertThat(orderPOD.getPodLineItems().get(0), is(lineItem1));
  }

  @Test
  public void shouldUpdatePOD() {
    Long createdBy = 1L;
    Long modifiedBy = 2L;
    OrderPOD orderPod = new OrderPOD();
    orderPod.setFacilityId(order.getRnr().getFacility().getId());
    orderPod.setPeriodId(order.getRnr().getPeriod().getId());
    orderPod.setProgramId(1L);
    orderPod.setOrderId(order.getId());
    orderPod.setOrderNumber("OYELL_FVR00000001R");
    orderPod.setCreatedBy(createdBy);
    orderPod.setModifiedBy(createdBy);

    mapper.insertPOD(orderPod);

    orderPod.setModifiedBy(modifiedBy);
    orderPod.setReceivedBy("acceptedBy");
    orderPod.setDeliveredBy("deliveredBy");
    Date receivedDate = new Date();
    orderPod.setReceivedDate(receivedDate);
    mapper.update(orderPod);

    OrderPOD updatedPOD = mapper.getPODById(orderPod.getId());
    assertThat(updatedPOD.getModifiedBy(), is(modifiedBy));
    assertThat(updatedPOD.getReceivedDate(), is(receivedDate));
    assertThat(updatedPOD.getReceivedBy(), is("acceptedBy"));
    assertThat(updatedPOD.getDeliveredBy(), is("deliveredBy"));
  }

  @Test
  public void shouldUpdatePODLineItem() throws Exception {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.setOrderNumber("OYELL_FVR00000001R");
    orderPod.setFacilityId(order.getRnr().getFacility().getId());
    orderPod.setPeriodId(order.getRnr().getPeriod().getId());
    orderPod.setProgramId(1L);
    mapper.insertPOD(orderPod);
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, null);
    mapper.insertPODLineItem(orderPodLineItem);

    orderPodLineItem.setNotes("notes");
    orderPodLineItem.setQuantityReceived(345);
    orderPodLineItem.setQuantityReturned(100);
    orderPodLineItem.setModifiedBy(3L);
    mapper.updateLineItem(orderPodLineItem);

    OrderPODLineItem lineItem = mapper.getPODLineItemsByPODId(orderPod.getId()).get(0);

    assertThat(lineItem.getModifiedBy(), is(orderPodLineItem.getModifiedBy()));
    assertThat(lineItem, is(orderPodLineItem));
  }
}
