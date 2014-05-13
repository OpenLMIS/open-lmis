/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
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
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProductBuilder.defaultProduct;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.rnr.builder.RequisitionBuilder.defaultRequisition;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ShipmentMapperIT {

  @Autowired
  private ShipmentMapper mapper;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  private RequisitionMapper requisitionMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private ProgramMapper programMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private ProcessingScheduleMapper processingScheduleMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  private SupplyLineMapper supplyLineMapper;

  @Test
  public void shouldInsertShippedLineItems() throws Exception {
    ShipmentLineItem shipmentLineItem = createShippedLineItem();
    mapper.insertShippedLineItem(shipmentLineItem);

    assertThat(shipmentLineItem.getId(), is(notNullValue()));

    String fetchShipmentFileInfoQuery = "Select * from shipment_line_items where id = ?";
    ResultSet shipmentFileInfoResultSet = queryExecutor.execute(fetchShipmentFileInfoQuery, shipmentLineItem.getId());

    shipmentFileInfoResultSet.next();

    assertThat(shipmentFileInfoResultSet.getLong("orderId"), is(shipmentLineItem.getOrderId()));
    assertThat(shipmentFileInfoResultSet.getString("productCode"), is(shipmentLineItem.getProductCode()));
    assertThat(shipmentFileInfoResultSet.getInt("quantityShipped"), is(shipmentLineItem.getQuantityShipped()));

    assertThat(shipmentFileInfoResultSet.getString("productName"), is(shipmentLineItem.getProductName()));
    assertThat(shipmentFileInfoResultSet.getString("dispensingUnit"), is(shipmentLineItem.getDispensingUnit()));
    assertThat(shipmentFileInfoResultSet.getString("productCategory"), is(shipmentLineItem.getProductCategory()));
    assertThat(shipmentFileInfoResultSet.getInt("packsToShip"), is(shipmentLineItem.getPacksToShip()));

    assertThat(shipmentFileInfoResultSet.getInt("productCategoryDisplayOrder"),
      is(shipmentLineItem.getProductCategoryDisplayOrder()));
    assertThat(shipmentFileInfoResultSet.getInt("productDisplayOrder"), is(shipmentLineItem.getProductDisplayOrder()));
    assertThat(shipmentFileInfoResultSet.getBoolean("fullSupply"), is(shipmentLineItem.getFullSupply()));
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
  public void shouldReturnLineItemsForAnOrder() throws Exception {
    ShipmentLineItem shipmentLineItem = createShippedLineItem();
    mapper.insertShippedLineItem(shipmentLineItem);

    Long orderId = shipmentLineItem.getOrderId();
    List<ShipmentLineItem> lineItems = mapper.getLineItems(orderId);

    assertThat(lineItems, hasItem(shipmentLineItem));
  }

  private ShipmentLineItem createShippedLineItem() {
    Product product = make(a(defaultProduct));
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    ProcessingSchedule processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);

    ProcessingPeriod period = make(a(ProcessingPeriodBuilder.defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(period);
    Program program = make(a(defaultProgram));
    programMapper.insert(program);
    Rnr requisition = make(a(defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(RequisitionBuilder.periodId, period.getId())));
    requisitionMapper.insert(requisition);

    Order order = new Order(requisition);
    order.setSupplyLine(createSupplyLine(facility, program));
    order.setStatus(OrderStatus.IN_ROUTE);
    order.setOrderNumber("OrderHIV00000001R");
    orderMapper.insert(order);

    productMapper.insert(product);

    return make(a(defaultShipmentLineItem, with(productCode, product.getCode()), with(orderId, order.getId()),
      with(quantityShipped, 23), with(shippedDate, new Date()), with(packedDate, new Date())));
  }

  private SupplyLine createSupplyLine(Facility facility, Program program) {
    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(facility);
    supplyLine.setProgram(program);
    supplyLine.setSupervisoryNode(supervisoryNode);
    supplyLine.setExportOrders(Boolean.TRUE);

    supplyLineMapper.insert(supplyLine);
    return supplyLine;
  }
}
