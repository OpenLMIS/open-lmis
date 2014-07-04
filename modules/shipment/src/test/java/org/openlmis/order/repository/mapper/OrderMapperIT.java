/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.order.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
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
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;
import static org.openlmis.core.builder.UserBuilder.*;
import static org.openlmis.core.domain.RightName.VIEW_ORDER;
import static org.openlmis.core.domain.RightType.FULFILLMENT;
import static org.openlmis.order.domain.OrderStatus.*;
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
  private UserMapper userMapper;
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

  @Autowired
  SupplyLineMapper supplyLineMapper;

  @Autowired
  private RoleRightsMapper roleRightsMapper;

  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;
  private ProcessingSchedule processingSchedule;
  private Facility facility;
  private ProcessingPeriod processingPeriod;
  private SupervisoryNode supervisoryNode;
  private SupplyLine supplyLine;

  @Before
  public void setUp() throws Exception {
    facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));
    processingScheduleMapper.insert(processingSchedule);
    processingPeriod = insertPeriod();
    supervisoryNode = insertSupervisoryNode();
    supplyLine = make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, facility),
      with(SupplyLineBuilder.supervisoryNode, supervisoryNode)));
    supplyLineMapper.insert(supplyLine);
  }

  @Test
  public void shouldInsertOrder() throws Exception {
    Rnr rnr = insertRequisition(1L);
    Order order = new Order(rnr);
    order.setStatus(OrderStatus.IN_ROUTE);
    order.setSupplyLine(supplyLine);
    order.setOrderNumber("OrdHIV00000001R");
    mapper.insert(order);
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM orders WHERE id = ?", order.getId());
    resultSet.next();
    assertThat(resultSet.getLong("id"), is(order.getId()));
    assertThat(resultSet.getString("orderNumber"), is("OrdHIV00000001R"));
  }

  @Test
  public void shouldGetOrdersForAPageGivenLimitAndOffset() throws Exception {
    insertOrder(1L);
    insertOrder(2L);
    Order order3 = insertOrder(3L);
    Order order4 = insertOrder(4L);

    Long userId = insertUserAndRoleForOrders();

    List<Order> orders = mapper.getOrders(2, 2, userId, VIEW_ORDER);

    assertThat(orders.size(), is(2));
    assertThat(orders.get(0).getId(), is(order3.getId()));
    assertThat(orders.get(1).getId(), is(order4.getId()));
  }


  @Test
  public void shouldGetShipmentFileInfoWhileFetchingOrders() throws Exception {
    Rnr rnr = insertRequisition(3L);
    Order order = new Order(rnr);
    order.setStatus(RELEASED);
    order.setSupplyLine(supplyLine);
    order.setOrderNumber("OrdHIV00000001R");
    mapper.insert(order);

    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setFileName("abc.csv");
    shipmentFileInfo.setProcessingError(false);
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);


    mapper.updateShipmentAndStatus(order.getOrderNumber(), RELEASED, shipmentFileInfo.getId());
    Long userId = insertUserAndRoleForOrders();
    List<Order> orders = mapper.getOrders(1, 0, userId, VIEW_ORDER);
    assertThat(orders.get(0).getShipmentFileInfo().getFileName(), is("abc.csv"));
    assertThat(orders.get(0).getShipmentFileInfo().isProcessingError(), is(false));
  }

  @Test
  public void shouldUpdateStatusAndShipmentIdForOrder() throws Exception {
    Order order = insertOrder(1L);

    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo("shipment.csv", true);
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);

    mapper.updateShipmentAndStatus(order.getOrderNumber(), PACKED, shipmentFileInfo.getId());

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM orders WHERE id = ?", order.getRnr().getId());

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
    assertThat(savedOrder.getSupplyLine().getId(), is(supplyLine.getId()));
  }

  @Test
  public void shouldGetOrderFileTemplate() throws Exception {
    List<OrderFileColumn> orderFileColumns = mapper.getOrderFileColumns();
    String[] expectedDataFieldLabels = {"header.order.number", "create.facility.code", "header.product.code",
      "header.product.name", "header.quantity.approved", "label.period", "header.order.date"};
    String[] expectedColumnLabels = {"Order number", "Facility code", "Product code", "Product name", "Approved quantity",
      "Period", "Order date"};
    assertThat(orderFileColumns.size(), is(expectedDataFieldLabels.length));
    for (int i = 0; i < expectedDataFieldLabels.length; i++) {
      assertThat(orderFileColumns.get(i).getDataFieldLabel(), is(expectedDataFieldLabels[i]));
      assertThat(orderFileColumns.get(i).getPosition(), is(i + 1));
      assertThat(orderFileColumns.get(i).getIncludeInOrderFile(), is(true));
      assertThat(orderFileColumns.get(i).getColumnLabel(), is(expectedColumnLabels[i]));
    }
  }

  @Test
  public void shouldDeleteAllOrderFileColumns() throws Exception {
    mapper.deleteOrderFileColumns();
    List<OrderFileColumn> orderFileColumns = mapper.getOrderFileColumns();
    assertThat(orderFileColumns.size(), is(0));
  }

  @Test
  public void shouldInsertOrderFileColumn() throws Exception {
    OrderFileColumn orderFileColumn = new OrderFileColumn();
    orderFileColumn.setColumnLabel("Red Label");
    orderFileColumn.setDataFieldLabel("More Red Label");
    orderFileColumn.setIncludeInOrderFile(true);
    orderFileColumn.setPosition(55);
    orderFileColumn.setOpenLmisField(true);
    mapper.insertOrderFileColumn(orderFileColumn);
    List<OrderFileColumn> orderFileColumns = mapper.getOrderFileColumns();
    assertThat(orderFileColumns.contains(orderFileColumn), is(true));
  }

  @Test
  public void shouldUpdateOrderStatusAndFtpComments() throws Exception {
    Order order = insertOrder(1L);
    order.setStatus(TRANSFER_FAILED);
    String ftpComment = "Supply line missing";
    order.setFtpComment(ftpComment);

    mapper.updateOrderStatus(order);

    Order savedOrder = mapper.getById(order.getId());
    assertThat(savedOrder.getStatus(), is(TRANSFER_FAILED));
    assertThat(savedOrder.getFtpComment(), is(ftpComment));
  }

  private Long insertUserAndRoleForOrders() throws SQLException {
    Long userId = 1l;

    User someUser = make(a(defaultUser, with(facilityId, facility.getId()), with(active, true)));
    userMapper.insert(someUser);

    Right right1 = new Right(VIEW_ORDER, FULFILLMENT);
    Role role = new Role("r1", "random description",asList(right1));
    Long roleId = Long.valueOf(roleRightsMapper.insertRole(role));
    role.setId(roleId);
    for (Right right : role.getRights()) {
      roleRightsMapper.createRoleRight(role, right.getName());
    }

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId,facilityId,roleId) values (?,?,?)", userId, facility.getId(), role.getId());
    return userId;
  }

  @Test
  public void shouldGetOrderStatusById() throws Exception {
    long programId = 1L;
    Order order = insertOrder(programId);

    OrderStatus status = mapper.getStatus(order.getOrderNumber());

    assertThat(status, is(order.getStatus()));
  }

  @Test
  public void shouldGet2PagesForGivenPageSizeOf3And4ROrders() throws Exception {
    insertOrder(1L);
    insertOrder(2L);
    insertOrder(3L);
    insertOrder(4L);

    Integer numberOfPages = mapper.getNumberOfPages(3);

    assertThat(numberOfPages, is(2));
  }

  @Test
  public void shouldGetOrdersBySupplyingFacilityIdsAndStatuses() throws Exception {
    insertOrderWithStatus(PACKED, 3L);
    insertOrderWithStatus(RELEASED, 5L);
    Long facilityId = supplyLine.getSupplyingFacility().getId();

    List<Order> orders = mapper.getByWarehouseIdsAndStatuses("{" + facilityId + "}", "{PACKED}");

    assertThat(orders.size(), is(1));

    List<Order> noOrders = mapper.getByWarehouseIdsAndStatuses("{" + facilityId + "}", "{RECEIVED}");

    assertThat(noOrders.size(), is(0));
  }

  private Order insertOrderWithStatus(OrderStatus status, Long programId) {
    Rnr rnr = insertRequisition(programId);
    Order order = new Order(rnr);
    order.setStatus(status);
    order.setSupplyLine(supplyLine);
    order.setOrderNumber("OrdHIV00000001R");
    mapper.insert(order);
    return order;
  }

  private Order insertOrder(Long programId) {
    Rnr rnr = insertRequisition(programId);
    Order order = new Order(rnr);
    order.setStatus(IN_ROUTE);
    order.setSupplyLine(supplyLine);
    order.setOrderNumber("OrdHIV00000001R");
    mapper.insert(order);
    return order;
  }

  private Rnr insertRequisition(Long programId) {
    Rnr rnr = make(a(defaultRequisition, with(RequisitionBuilder.facility, facility),
      with(periodId, processingPeriod.getId()), with(program, new Program(programId))));
    requisitionMapper.insert(rnr);
    return rnr;
  }

  private ProcessingPeriod insertPeriod() {
    ProcessingPeriod processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, processingSchedule.getId())));
    processingPeriodMapper.insert(processingPeriod);
    return processingPeriod;
  }

  private SupervisoryNode insertSupervisoryNode() {
    supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);

    supervisoryNodeMapper.insert(supervisoryNode);
    return supervisoryNode;
  }
}
