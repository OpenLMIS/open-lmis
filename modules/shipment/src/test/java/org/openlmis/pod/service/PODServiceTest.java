/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.repository.PODRepository;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.service.ShipmentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.MANAGE_POD;
import static org.openlmis.order.domain.OrderStatus.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;


@Category(IntegrationTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(PODService.class)
public class PODServiceTest {

  @Mock
  private PODRepository podRepository;

  @Mock
  private OrderService orderService;

  @Mock
  private ProductService productService;

  @Mock
  private RequisitionService requisitionService;

  @Mock
  private ShipmentService shipmentService;

  @Mock
  private FulfillmentPermissionService fulfillmentPermissionService;

  @InjectMocks
  private PODService podService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private Long podId;
  private Long orderId;
  private Long userId;
  private Long facilityId;
  private OrderPOD orderPod;

  @Before
  public void setUp() throws Exception {
    podService = spy(podService);
    podId = 1L;
    orderId = 2L;
    userId = 3L;
    facilityId = 4L;
    orderPod = new OrderPOD(podId);
    orderPod.setOrderId(orderId);
    orderPod.setCreatedBy(userId);
  }

  @Test
  public void shouldCreatePODFromPackedOrder() throws Exception {
    long orderId = 4L;
    OrderPOD orderPOD = spy(new OrderPOD(orderId, 8L));

    doNothing().when(podService).checkPermissions(orderPOD);
    when(orderService.hasStatus(orderId, PACKED)).thenReturn(true);
    List<ShipmentLineItem> shipmentLineItems = asList(mock(ShipmentLineItem.class));
    when(shipmentService.getLineItems(orderId)).thenReturn(shipmentLineItems);
    doNothing().when(orderPOD).fillPodLineItems(shipmentLineItems);
    when(podRepository.insert(orderPOD)).thenReturn(orderPOD);

    OrderPOD pod = podService.createPOD(orderPOD);

    verify(orderPOD).fillPodLineItems(shipmentLineItems);
    assertThat(pod, is(orderPOD));
  }

  @Test
  public void shouldCreatePODFromReleasedOrder() throws Exception {
    long orderId = 6L;
    OrderPOD orderPOD = spy(new OrderPOD(orderId, 8L));

    doNothing().when(podService).checkPermissions(orderPOD);
    when(orderService.hasStatus(orderId, RELEASED, READY_TO_PACK, TRANSFER_FAILED)).thenReturn(true);
    Rnr requisition = make(a(RequisitionBuilder.defaultRequisition));
    when(requisitionService.getFullRequisitionById(orderId)).thenReturn(requisition);
    doNothing().when(orderPOD).fillPODWithRequisition(requisition);
    when(podRepository.insert(orderPOD)).thenReturn(orderPOD);

    OrderPOD pod = podService.createPOD(orderPOD);

    verify(orderPOD).fillPODWithRequisition(requisition);
    verify(podRepository).insert(orderPOD);
    assertThat(pod, is(orderPOD));
  }

  @Test
  public void shouldGetPODByOrderId() {
    Long orderId = 2l;
    OrderPOD expectedOrderPOD = new OrderPOD();
    when(podRepository.getPODByOrderId(orderId)).thenReturn(expectedOrderPOD);

    OrderPOD savedOrderPOD = podService.getPODByOrderId(orderId);

    verify(podRepository).getPODByOrderId(orderId);
    assertThat(savedOrderPOD, is(expectedOrderPOD));
  }

  @Test
  public void shouldThrowErrorIfUserDoesNotHavePermissionOnGivenWareHouse() {
    Order order = new Order(orderId);
    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility(facilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);
    order.setSupplyLine(supplyLine);
    when(orderService.getOrder(orderId)).thenReturn(order);
    when(fulfillmentPermissionService.hasPermission(userId, facilityId, MANAGE_POD)).thenReturn(false);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.permission.denied");

    podService.createPOD(orderPod);
  }


  @Test
  public void shouldGetPODWithLineItemsById() throws Exception {
    podService.getPodById(podId);
    verify(podRepository).getPODWithLineItemsById(podId);
  }
}
