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
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.repository.PODRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.MANAGE_POD;
import static org.powermock.api.mockito.PowerMockito.whenNew;


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
  private MessageService messageService;

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
  private POD pod;

  @Before
  public void setUp() throws Exception {
    podId = 1l;
    orderId = 2l;
    userId = 3l;
    facilityId = 4l;
    pod = new POD(podId);
    pod.setOrderId(orderId);
    pod.setCreatedBy(userId);

  }

  @Test
  public void shouldUpdatePODAndLineItemsIfValid() throws Exception {
    PODLineItem podLineItem1 = mock(PODLineItem.class);
    ArrayList invalidProducts = mock(ArrayList.class);
    PODLineItem podLineItem2 = mock(PODLineItem.class);
    List<PODLineItem> lineItems = asList(podLineItem1, podLineItem2);
    pod.setPodLineItems(lineItems);
    Order order = mock(Order.class);
    whenNew(Order.class).withArguments(orderId).thenReturn(order);
    whenNew(ArrayList.class).withNoArguments().thenReturn(invalidProducts);
    when(invalidProducts.size()).thenReturn(0);
    when(orderService.getOrder(orderId)).thenReturn(order);
    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility(facilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);
    order.setSupplyLine(supplyLine);
    when(order.getSupplyLine()).thenReturn(supplyLine);
    when(fulfillmentPermissionService.hasPermission(userId, facilityId, MANAGE_POD)).thenReturn(true);
    podService.updatePOD(pod);

    verify(order).setStatus(OrderStatus.RECEIVED);
    verify(orderService).updateOrderStatus(order);
    verify(podRepository).insertPOD(pod);
    verify(podLineItem1).setPodId(podId);
    verify(podLineItem2).setPodId(podId);
    verify(podRepository).insertPODLineItem(podLineItem1);
    verify(podRepository).insertPODLineItem(podLineItem2);
  }


  @Test
  public void shouldThrowErrorIfProductIsNotValid() throws Exception {
    ArrayList invalidProducts = mock(ArrayList.class);
    String productCode = "productCode1";
    List<PODLineItem> lineItems = asList(new PODLineItem(podId, productCode, 100), new PODLineItem(podId, "productCode2", 100));
    pod.setPodLineItems(lineItems);
    when(productService.getIdForCode(productCode)).thenReturn(null);
    whenNew(ArrayList.class).withNoArguments().thenReturn(invalidProducts);
    when(invalidProducts.size()).thenReturn(1);
    Order order = mock(Order.class);
    when(orderService.getOrder(orderId)).thenReturn(order);
    SupplyLine supplyLine = new SupplyLine();
    Facility supplyingFacility = new Facility(facilityId);
    supplyLine.setSupplyingFacility(supplyingFacility);
    order.setSupplyLine(supplyLine);
    when(order.getSupplyLine()).thenReturn(supplyLine);
    when(messageService.message("error.invalid.product.code", invalidProducts.toString())).thenReturn("Invalid Product");
    when(fulfillmentPermissionService.hasPermission(userId, facilityId, MANAGE_POD)).thenReturn(true);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Invalid Product");

    podService.updatePOD(pod);

  }

  @Test
  public void shouldGetPODByOrderId() {
    Long orderId = 2l;
    POD expectedPOD = new POD();
    when(podRepository.getPODByOrderId(orderId)).thenReturn(expectedPOD);
    POD savedPOD = podService.getPODByOrderId(orderId);
    verify(podRepository).getPODByOrderId(orderId);
    assertThat(savedPOD, is(expectedPOD));
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
    expectedException.expectMessage("error.pod.permission.denied");

    podService.updatePOD(pod);
  }

}
