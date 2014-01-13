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
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.repository.PODRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.domain.Right.MANAGE_POD;
import static org.powermock.api.mockito.PowerMockito.*;


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
    podId = 1L;
    orderId = 2L;
    userId = 3L;
    facilityId = 4L;
    orderPod = new OrderPOD(podId);
    orderPod.setOrderId(orderId);
    orderPod.setCreatedBy(userId);
  }

  @Test
  public void shouldInsertAndGetPODWithLineItems() throws Exception {
    OrderPODLineItem orderPodLineItem1 = mock(OrderPODLineItem.class);
    OrderPODLineItem orderPodLineItem2 = mock(OrderPODLineItem.class);

    OrderPOD spyOrderPod = spy(orderPod);
    spyOrderPod.setPodLineItems(asList(orderPodLineItem1, orderPodLineItem2));
    whenNew(OrderPOD.class).withNoArguments().thenReturn(spyOrderPod);

    Order order = mock(Order.class);
    whenNew(Order.class).withArguments(orderId).thenReturn(order);

    when(orderService.getOrder(orderId)).thenReturn(order);
    when(spyOrderPod.getOrderId()).thenReturn(orderId);

    when(podRepository.getPODByOrderId(orderId)).thenReturn(null);

    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility(facilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);
    order.setSupplyLine(supplyLine);

    when(order.getSupplyingFacility()).thenReturn(supplyingFacility);
    when(fulfillmentPermissionService.hasPermission(userId, facilityId, MANAGE_POD)).thenReturn(true);

    Rnr requisition = new Rnr();
    when(requisitionService.getFullRequisitionById(spyOrderPod.getOrderId())).thenReturn(requisition);

    doNothing().when(spyOrderPod).fillPOD(requisition);
    doNothing().when(spyOrderPod).fillPodLineItems(requisition.getAllLineItems());

    podService.getPOD(orderId, userId);

    verify(podRepository).insertPOD(spyOrderPod);
    verify(podRepository).getPODByOrderId(orderId);
    verify(podRepository).insertPODLineItem(orderPodLineItem1);
    verify(podRepository).insertPODLineItem(orderPodLineItem2);
    verify(podRepository).getPODWithLineItemsByOrderId(orderId);
  }

  @Test
  public void shouldGetPODWithLineItemsIfExists() throws Exception {
    OrderPODLineItem orderPodLineItem1 = mock(OrderPODLineItem.class);
    OrderPODLineItem orderPodLineItem2 = mock(OrderPODLineItem.class);

    OrderPOD spyOrderPod = spy(orderPod);
    spyOrderPod.setPodLineItems(asList(orderPodLineItem1, orderPodLineItem2));

    Order order = mock(Order.class);
    whenNew(Order.class).withArguments(orderId).thenReturn(order);

    when(orderService.getOrder(orderId)).thenReturn(order);
    when(spyOrderPod.getOrderId()).thenReturn(orderId);

    when(podRepository.getPODByOrderId(orderId)).thenReturn(spyOrderPod);

    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility(facilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);
    order.setSupplyLine(supplyLine);

    when(order.getSupplyingFacility()).thenReturn(supplyingFacility);
    when(fulfillmentPermissionService.hasPermission(userId, facilityId, MANAGE_POD)).thenReturn(true);

    podService.getPOD(orderId, userId);

    verify(podRepository).getPODByOrderId(orderId);
    verify(podRepository).getPODWithLineItemsByOrderId(orderId);
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

    podService.getPOD(orderId, userId);
  }
}
