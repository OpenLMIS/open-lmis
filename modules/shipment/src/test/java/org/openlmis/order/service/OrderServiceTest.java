/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.service;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.builder.SupplyLineBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.OrderConfigurationRepository;
import org.openlmis.core.service.*;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
import org.openlmis.order.domain.DateFormat;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.FacilityBuilder.*;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.SupplyLineBuilder.defaultSupplyLine;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.order.domain.DateFormat.*;
import static org.openlmis.order.domain.OrderStatus.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderService.class)
public class OrderServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private OrderConfigurationRepository orderConfigurationRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private RequisitionService requisitionService;

  @Mock
  private OrderEventService orderEventService;

  @Mock
  private SupplyLineService supplyLineService;

  @Mock
  private RoleAssignmentService roleAssignmentService;

  @Mock
  private FulfillmentPermissionService fulfillmentPermissionService;

  @Mock
  private UserService userService;

  @Mock
  private ProgramService programService;

  @Mock
  private StatusChangeEventService statusChangeEventService;

  @InjectMocks
  private OrderService orderService;

  @Test
  public void shouldConvertRequisitionsToOrderWithStatusInRoute() throws Exception {
    Program program = new Program(3L);
    program.setCode("HIV");
    Long userId = 1L;
    Rnr rnr = new Rnr();
    rnr.setId(1L);
    rnr.setSupervisoryNodeId(1L);
    rnr.setProgram(program);

    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(new Facility(99L));
    supplyLine.setExportOrders(Boolean.TRUE);

    when(requisitionService.getLWById(1L)).thenReturn(rnr);
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    whenNew(SupervisoryNode.class).withArguments(1l).thenReturn(supervisoryNode);
    when(supplyLineService.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    List<Rnr> rnrList = new ArrayList<>();
    rnrList.add(rnr);
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(rnr);
    when(fulfillmentPermissionService.hasPermissionOnWarehouse(userId, 99L, CONVERT_TO_ORDER)).thenReturn(true);
    OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration("Order", true, true, true, true);
    when(programService.getById(program.getId())).thenReturn(program);
    when(orderConfigurationRepository.getOrderNumberConfiguration()).thenReturn(orderNumberConfiguration);
    orderService.convertToOrder(rnrList, userId);

    Order order = new Order(rnr);

    order.setStatus(OrderStatus.IN_ROUTE);
    order.setSupplyLine(supplyLine);
    order.setOrderNumber("OrderHIV00000001R");
    verify(orderRepository).save(order);
    verify(supplyLineService).getSupplyLineBy(supervisoryNode, program);
    verify(requisitionService).getLWById(rnr.getId());
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
    verify(orderEventService).notifyForStatusChange(order);
    assertThat(order.getSupplyLine(), is(supplyLine));
  }

  @Test
  public void shouldConvertRequisitionsToOrderWithStatusReadyToPack() throws Exception {
    Long userId = 1L;
    Long supplyingFacilityId = 99L;

    Program program = new Program(3L);
    program.setCode("HIV");
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    Rnr rnr = new Rnr();
    rnr.setId(1L);
    rnr.setSupervisoryNodeId(1L);
    rnr.setProgram(program);

    Facility supplyingFacility = new Facility(supplyingFacilityId);

    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(supplyingFacility);
    supplyLine.setExportOrders(Boolean.FALSE);

    when(requisitionService.getLWById(1L)).thenReturn(rnr);
    whenNew(SupervisoryNode.class).withArguments(1l).thenReturn(supervisoryNode);
    when(supplyLineService.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    List<Rnr> rnrList = new ArrayList<>();
    rnrList.add(rnr);
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(rnr);
    OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration("Order", true, true, true, true);
    when(orderConfigurationRepository.getOrderNumberConfiguration()).thenReturn(orderNumberConfiguration);
    when(programService.getById(program.getId())).thenReturn(program);
    when(fulfillmentPermissionService.hasPermissionOnWarehouse(userId, supplyingFacilityId, CONVERT_TO_ORDER)).thenReturn(true);
    when(userService.getUsersWithRightOnWarehouse(supplyingFacilityId, FACILITY_FILL_SHIPMENT)).thenReturn(EMPTY_LIST);

    orderService.convertToOrder(rnrList, userId);

    Order order = new Order(rnr);
    order.setStatus(OrderStatus.READY_TO_PACK);
    order.setSupplyLine(supplyLine);
    order.setOrderNumber("OrderHIV00000001R");
    verify(orderRepository).save(order);
    verify(supplyLineService).getSupplyLineBy(supervisoryNode, program);
    verify(requisitionService).getLWById(rnr.getId());
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
    verify(userService).getUsersWithRightOnWarehouse(supplyingFacilityId, FACILITY_FILL_SHIPMENT);
    assertThat(order.getSupplyLine(), is(supplyLine));
  }

  @Test
  public void shouldThrowExceptionWhileConvertingRequisitionsToOrderIfUserDoesNotHaveRights() throws Exception {
    Program program = new Program();
    Long userId = 1L;
    Rnr rnr = new Rnr();
    rnr.setId(1L);
    rnr.setSupervisoryNodeId(1L);
    rnr.setProgram(program);

    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(new Facility(99L));
    supplyLine.setExportOrders(Boolean.FALSE);

    when(requisitionService.getLWById(1L)).thenReturn(rnr);
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    whenNew(SupervisoryNode.class).withArguments(1l).thenReturn(supervisoryNode);
    when(supplyLineService.getSupplyLineBy(supervisoryNode, program)).thenReturn(supplyLine);

    List<Rnr> rnrList = new ArrayList<>();
    rnrList.add(rnr);
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(rnr);
    when(fulfillmentPermissionService.hasPermissionOnWarehouse(userId, 99L, CONVERT_TO_ORDER)).thenReturn(false);

    expectedException.expect(AccessDeniedException.class);
    expectedException.expectMessage("user.not.authorized");

    orderService.convertToOrder(rnrList, userId);

    verify(supplyLineService).getSupplyLineBy(supervisoryNode, program);
    verify(requisitionService).getLWById(rnr.getId());
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
  }

  @Test
  @Ignore
  public void shouldConvertRequisitionsToOrderWithStatusTransferFailed() throws Exception {
    String SUPPLY_LINE_MISSING_COMMENT = "order.ftpComment.supplyline.missing";
    Program program = new Program(3L);
    program.setCode("HIV");
    Long userId = 1L;
    Rnr rnr = new Rnr();
    rnr.setId(1L);
    rnr.setSupervisoryNodeId(1L);
    rnr.setProgram(program);

    when(requisitionService.getLWById(1L)).thenReturn(rnr);
    SupervisoryNode supervisoryNode = new SupervisoryNode(1L);
    whenNew(SupervisoryNode.class).withArguments(1l).thenReturn(supervisoryNode);
    when(supplyLineService.getSupplyLineBy(supervisoryNode, program)).thenReturn(null);
    when(requisitionService.getFullRequisitionById(1L)).thenReturn(rnr);
    OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration("Order", true, true, true, true);
    when(orderConfigurationRepository.getOrderNumberConfiguration()).thenReturn(orderNumberConfiguration);
    when(programService.getById(program.getId())).thenReturn(program);

    List<Rnr> rnrList = new ArrayList<>();
    rnrList.add(rnr);

    orderService.convertToOrder(rnrList, userId);

    Order order = new Order(rnr);
    order.setStatus(OrderStatus.TRANSFER_FAILED);
    order.setFtpComment(SUPPLY_LINE_MISSING_COMMENT);
    order.setSupplyLine(null);
    order.setOrderNumber("OrderHIV00000001R");
    verify(orderRepository).save(order);
    verify(supplyLineService).getSupplyLineBy(supervisoryNode, program);
    verify(requisitionService).getLWById(rnr.getId());
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
  }

  @Test
  public void shouldGetOrdersFilledWithRequisition() throws Exception {
    orderService.setPageSize("3");
    Rnr rnr1 = make(a(defaultRequisition, with(id, 78L)));
    final Order order1 = new Order();
    order1.setRnr(rnr1);

    Rnr rnr2 = make(a(defaultRequisition, with(periodId, 2L), with(id, 72L)));

    final Order order2 = new Order();
    order2.setRnr(rnr2);

    List<Order> expectedOrders = new ArrayList<Order>() {{
      add(order1);
      add(order2);
    }};

    when(orderRepository.getOrdersForPage(2, 3, 1l, Right.VIEW_ORDER)).thenReturn(expectedOrders);
    when(requisitionService.getFullRequisitionById(rnr1.getId())).thenReturn(rnr1);
    when(requisitionService.getFullRequisitionById(rnr2.getId())).thenReturn(rnr2);

    List<Order> orders = orderService.getOrdersForPage(2, 1l, Right.VIEW_ORDER);

    assertThat(orders, is(expectedOrders));
    verify(orderRepository).getOrdersForPage(2, 3, 1l, Right.VIEW_ORDER);
    verify(requisitionService).getFullRequisitionById(rnr1.getId());
    verify(requisitionService).getFullRequisitionById(rnr2.getId());
  }

  @Test
  public void shouldSetReleasedForAllOrdersIfErrorInShipment() throws Exception {
    Order order1 = new Order(make(a(defaultRequisition, with(id, 123L))));
    order1.setOrderNumber("OYELL_FVR00000123R");
    Order order2 = new Order(make(a(defaultRequisition, with(id, 456L), with(RequisitionBuilder.facility, make(a(defaultFacility, with(code, "F3333")))))));
    order1.setOrderNumber("OYELL_FVR00000456R");
    order1.setStatus(RELEASED);
    order2.setStatus(RELEASED);

    Set<String> orderNumbers = new LinkedHashSet<>();
    orderNumbers.add(order1.getOrderNumber());
    orderNumbers.add(order2.getOrderNumber());
    long shipmentId = 678L;

    boolean processingError = true;

    Long supplyingFacilityId = 99L;
    Facility supplyingFacility = new Facility(supplyingFacilityId);
    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(supplyingFacility);

    order1.setSupplyLine(supplyLine);
    order2.setSupplyLine(supplyLine);

    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo("shipmentFile.csv", processingError);
    shipmentFileInfo.setId(shipmentId);

    when(orderRepository.getByOrderNumber(order1.getOrderNumber())).thenReturn(order1);
    when(orderRepository.getByOrderNumber(order2.getOrderNumber())).thenReturn(order2);
    when(requisitionService.getFullRequisitionById(order1.getRnr().getId())).thenReturn(order1.getRnr());
    when(requisitionService.getFullRequisitionById(order2.getRnr().getId())).thenReturn(order2.getRnr());

    orderService.updateStatusAndShipmentIdForOrders(orderNumbers, shipmentFileInfo);

    verify(orderRepository).getByOrderNumber(order1.getOrderNumber());
    verify(orderRepository).updateStatusAndShipmentIdForOrder(order1.getOrderNumber(), RELEASED, shipmentId);
    verify(orderEventService).notifyForStatusChange(order1);

    verify(orderRepository).getByOrderNumber(order2.getOrderNumber());
    verify(orderRepository).updateStatusAndShipmentIdForOrder(order2.getOrderNumber(), RELEASED, shipmentId);
    verify(orderEventService).notifyForStatusChange(order2);
    verify(userService, never()).getUsersWithRightOnWarehouse(supplyingFacilityId, MANAGE_POD);
  }

  @Test
  public void shouldSetPackedStatusForAllOrdersIfNoErrorInShipment() throws Exception {
    Order order1 = new Order(make(a(defaultRequisition, with(id, 123L))));
    order1.setOrderNumber("OYELL_FVR00000123R");
    Order order2 = new Order(make(a(defaultRequisition, with(id, 456L), with(RequisitionBuilder.facility, make(a(defaultFacility, with(code, "F3333")))))));
    order1.setOrderNumber("OYELL_FVR00000456R");

    Set<String> orderNumbers = new LinkedHashSet<>();
    orderNumbers.add(order1.getOrderNumber());
    orderNumbers.add(order2.getOrderNumber());
    long shipmentId = 678L;

    boolean processingError = false;

    Long supplyingFacilityId = 99L;
    Facility supplyingFacility = new Facility(supplyingFacilityId);
    SupplyLine supplyLine = new SupplyLine();
    supplyLine.setSupplyingFacility(supplyingFacility);

    order1.setSupplyLine(supplyLine);
    order2.setSupplyLine(supplyLine);
    order1.setStatus(PACKED);
    order2.setStatus(PACKED);

    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo("shipmentFile.csv", processingError);
    shipmentFileInfo.setId(shipmentId);

    when(orderRepository.getByOrderNumber(order1.getOrderNumber())).thenReturn(order1);
    when(orderRepository.getByOrderNumber(order2.getOrderNumber())).thenReturn(order2);
    when(requisitionService.getFullRequisitionById(order1.getRnr().getId())).thenReturn(order1.getRnr());
    when(requisitionService.getFullRequisitionById(order2.getRnr().getId())).thenReturn(order2.getRnr());

    when(userService.getUsersWithRightOnWarehouse(supplyingFacilityId, MANAGE_POD)).thenReturn(EMPTY_LIST);

    orderService.updateStatusAndShipmentIdForOrders(orderNumbers, shipmentFileInfo);

    verify(orderRepository).updateStatusAndShipmentIdForOrder(order1.getOrderNumber(), PACKED, shipmentId);
    verify(orderRepository).updateStatusAndShipmentIdForOrder(order2.getOrderNumber(), PACKED, shipmentId);
    verify(userService, times(2)).getUsersWithRightOnWarehouse(supplyingFacilityId, MANAGE_POD);
  }

  @Test
  public void shouldGetOrderWithoutUnorderedProducts() {
    Long orderId = 1L;
    Long rnrId = 1L;
    Order order = new Order();

    Rnr rnr = new Rnr();
    rnr.setId(rnrId);
    RnrLineItem rnrLineItem = new RnrLineItem();
    List<RnrLineItem> lineItems = new ArrayList<>();
    rnrLineItem.setPacksToShip(0);
    lineItems.add(rnrLineItem);
    rnr.setFullSupplyLineItems(lineItems);
    rnr.setNonFullSupplyLineItems(lineItems);
    order.setRnr(rnr);

    when(orderRepository.getById(orderId)).thenReturn(order);
    when(requisitionService.getFullRequisitionById(rnr.getId())).thenReturn(rnr);

    Order expectedOrder = orderService.getOrder(orderId);

    verify(orderRepository).getById(orderId);
    verify(requisitionService).getFullRequisitionById(rnr.getId());
    assertThat(order.getRnr().getFullSupplyLineItems().size(), is(0));
    assertThat(order.getRnr().getNonFullSupplyLineItems().size(), is(0));
    assertThat(expectedOrder, is(order));
  }

  @Test
  public void shouldGetOrderFileTemplateWithConfiguration() throws Exception {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    List<OrderFileColumn> orderFileColumns = new ArrayList<>();
    OrderFileTemplateDTO expectedOrderFileTemplateDTO = new OrderFileTemplateDTO(orderConfiguration, orderFileColumns);
    when(orderConfigurationRepository.getConfiguration()).thenReturn(orderConfiguration);
    when(orderRepository.getOrderFileTemplate()).thenReturn(orderFileColumns);
    OrderFileTemplateDTO actualOrderFileTemplateDTO = orderService.getOrderFileTemplateDTO();
    verify(orderConfigurationRepository).getConfiguration();
    verify(orderRepository).getOrderFileTemplate();
    assertThat(actualOrderFileTemplateDTO, is(expectedOrderFileTemplateDTO));
  }

  @Test
  public void shouldSaveOrderFileColumnsWithConfiguration() throws Exception {
    OrderConfiguration orderConfiguration = new OrderConfiguration();
    List<OrderFileColumn> orderFileColumns = new ArrayList<>();
    OrderFileTemplateDTO orderFileTemplateDTO = new OrderFileTemplateDTO(orderConfiguration, orderFileColumns);
    Long userId = 1L;
    orderService.saveOrderFileTemplate(orderFileTemplateDTO, userId);
    verify(orderConfigurationRepository).update(orderConfiguration);
    verify(orderRepository).saveOrderFileColumns(orderFileColumns, userId);
  }

  @Test
  public void shouldGetAllDateFormats() throws Exception {
    List<DateFormat> dateFormats = new ArrayList<>(orderService.getAllDateFormats());
    List<DateFormat> expectedDateFormats = asList(DATE_1, DATE_2, DATE_3, DATE_4, DATE_5, DATE_6, DATE_7, DATE_8, DATE_9, DATE_10,
      DATE_11, DATE_12, DATE_13, DATE_14, DATE_15, DATE_16, DATE_17, DATE_18, DATE_19, DATE_20,
      DATE_21, DATE_22, DATE_23, DATE_24, DATE_25, DATE_26, DATE_27, DATE_28, DATE_29, DATE_30
    );

    assertThat(dateFormats, is(expectedDateFormats));
  }

  @Test
  public void shouldUpdateOrderStatusAndFtpComment() throws Exception {
    Order order = new Order(make(a(defaultRequisition, with(id, 123L))));
    when(requisitionService.getFullRequisitionById(order.getRnr().getId())).thenReturn(order.getRnr());

    orderService.updateOrderStatus(order);
    verify(orderRepository).updateOrderStatus(order);
    verify(orderEventService).notifyForStatusChange(order);
  }

  @Test
  public void shouldReturnTrueIfOrderIsReleased() throws Exception {
    String orderNumber = "OYELL_FVR00000123R";
    when(orderRepository.getStatus(orderNumber)).thenReturn(RELEASED);

    assertThat(orderService.isShippable(orderNumber), is(true));

    verify(orderRepository).getStatus(orderNumber);
  }

  @Test
  public void shouldReturnTrueIfOrderInOneOfTheStatuses() throws Exception {
    String orderNumber = "OYELL_FVR00000123R";

    when(orderRepository.getStatus(orderNumber)).thenReturn(RELEASED);

    assertTrue(orderService.hasStatus(orderNumber, PACKED, RELEASED));
  }

  @Test
  public void shouldReturnFalseIfOrderInOneOfTheStatuses() throws Exception {
    String orderNumber = "OYELL_FVR00000123R";
    when(orderRepository.getStatus(orderNumber)).thenReturn(RELEASED);

    assertFalse(orderService.hasStatus(orderNumber, PACKED, TRANSFER_FAILED));
  }

  @Test
  public void shouldGetPageSize() throws Exception {
    orderService.setPageSize("4");

    Integer pageSize = orderService.getPageSize();

    assertThat(pageSize, is(4));
  }

  @Test
  public void shouldReturnTrueIfOrderIsNotShippable() throws Exception {
    String orderNumber = "OYELL_FVR00000123R";
    when(orderRepository.getStatus(orderNumber))
      .thenReturn(IN_ROUTE)
      .thenReturn(PACKED)
      .thenReturn(TRANSFER_FAILED)
      .thenReturn(READY_TO_PACK);

    assertThat(orderService.isShippable(orderNumber), is(false));
    assertThat(orderService.isShippable(orderNumber), is(false));
    assertThat(orderService.isShippable(orderNumber), is(false));
    assertThat(orderService.isShippable(orderNumber), is(false));

    verify(orderRepository, times(4)).getStatus(orderNumber);

  }

  @Test
  public void shouldSearchOrdersByStatus() throws Exception {
    Order order = new Order();
    order.setRnr(new Rnr(13l));
    List<Order> expectedOrders = asList(order);
    when(roleAssignmentService.getFulfilmentRolesWithRight(3L, MANAGE_POD)).thenReturn(asList(new FulfillmentRoleAssignment(3L, 4l, new ArrayList<Long>())));
    when(requisitionService.getFullRequisitionById(13L)).thenReturn(new Rnr());
    when(orderRepository.searchByWarehousesAndStatuses(asList(4l), asList(RELEASED, PACKED, TRANSFER_FAILED, READY_TO_PACK))).thenReturn(expectedOrders);

    List<Order> returnedOrders = orderService.searchByStatusAndRight(3l, MANAGE_POD, asList(RELEASED, PACKED, TRANSFER_FAILED, READY_TO_PACK));

    assertThat(returnedOrders, is(expectedOrders));
  }

  @Test
  public void shouldSortOrdersBasedOnSupplyingFacilityNameProgramNameAndThenCreationDate() throws Exception {
    Rnr rnrForHIV = make(a(RequisitionBuilder.defaultRequisition, with(id, 2L), with(program, make(a(defaultProgram, with(ProgramBuilder.programName, "HIV"))))));
    Rnr rnrForTB = make(a(RequisitionBuilder.defaultRequisition, with(id, 4L), with(program, make(a(defaultProgram, with(ProgramBuilder.programName, "TB"))))));
    Rnr rnrForMalaria = make(a(RequisitionBuilder.defaultRequisition, with(id, 6L), with(program, make(a(defaultProgram, with(ProgramBuilder.programName, "MALARIA"))))));

    Order order1 = new Order(new Rnr(2L));
    order1.setSupplyLine(make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, make(a(FacilityBuilder.defaultFacility, with(name, "F10")))))));

    Order order2 = new Order(new Rnr(4L));
    order2.setSupplyLine(make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, make(a(FacilityBuilder.defaultFacility, with(name, "F11")))))));

    Order order3 = new Order(new Rnr(6L));
    order3.setSupplyLine(make(a(defaultSupplyLine, with(SupplyLineBuilder.facility, make(a(FacilityBuilder.defaultFacility, with(name, "F10")))))));

    List<Order> expectedOrders = asList(order1, order2, order3);
    when(roleAssignmentService.getFulfilmentRolesWithRight(3L, MANAGE_POD)).thenReturn(asList(new FulfillmentRoleAssignment(3L, 4l, new ArrayList<Long>())));
    when(requisitionService.getFullRequisitionById(2L)).thenReturn(rnrForHIV);
    when(requisitionService.getFullRequisitionById(4L)).thenReturn(rnrForTB);
    when(requisitionService.getFullRequisitionById(6L)).thenReturn(rnrForMalaria);
    when(orderRepository.searchByWarehousesAndStatuses(asList(4l), asList(RELEASED, PACKED, TRANSFER_FAILED, READY_TO_PACK))).thenReturn(expectedOrders);

    List<Order> returnedOrders = orderService.searchByStatusAndRight(3l, MANAGE_POD, asList(RELEASED, PACKED, TRANSFER_FAILED, READY_TO_PACK));

    assertThat(returnedOrders.get(0).getId(), is(2L));
    assertThat(returnedOrders.get(1).getId(), is(6L));
    assertThat(returnedOrders.get(2).getId(), is(4L));
  }

  @Test
  public void shouldGetNumberOfPages() throws Exception {
    orderService.setPageSize("3");
    when(orderRepository.getNumberOfPages(3)).thenReturn(1);

    Integer numberOfPages = orderService.getNumberOfPages();

    assertThat(numberOfPages, is(1));
  }
}
