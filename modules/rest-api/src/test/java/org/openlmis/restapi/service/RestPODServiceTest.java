/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.restapi.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Product;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.service.PODService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RestPODServiceTest {

  @InjectMocks
  private RestPODService restPODService;

  @Mock
  private OrderService orderService;

  @Mock
  private PODService podService;

  @Mock
  private ProductService productService;

  @Mock
  private RequisitionService requisitionService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldUpdatePOD() {
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem();
    orderPodLineItem.setProductCode("productCode");
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderId(4L);
    orderPod.setOrderNumber("ORD");
    orderPod.setPodLineItems(asList(orderPodLineItem));

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getByOrderNumber("ORD")).thenReturn(new Order(4L));
    when(productService.getByCode("productCode")).thenReturn(new Product());
    doNothing().when(podService).checkPermissions(orderPod);
    when(podService.getPODByOrderId(4L)).thenReturn(null);
    Rnr rnr = new Rnr();
    when(requisitionService.getLWById(orderPod.getOrderId())).thenReturn(rnr);

    doNothing().when(spyOrderPod).fillPOD(rnr);
    doNothing().when(podService).insertPOD(orderPod);
    doNothing().when(podService).insertPODLineItem(orderPodLineItem);
    doNothing().when(podService).updateOrderStatus(orderPod);

    restPODService.updatePOD(spyOrderPod, 1L);

    verify(podService).checkPermissions(orderPod);
    verify(podService).getPODByOrderId(4L);
    verify(podService).insertPOD(orderPod);
    verify(podService).insertPODLineItem(orderPodLineItem);
  }

  @Test
  public void shouldThrowErrorIfInvalidOrder() throws Exception {
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderNumber("ON123");

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getByOrderNumber("ON123")).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.invalid.order");

    restPODService.updatePOD(spyOrderPod, 1L);
  }

  @Test
  public void shouldThrowErrorIfPODAlreadyConfirmed() throws Exception {
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderNumber("ON123");

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getByOrderNumber("ON123")).thenReturn(new Order(4L));

    when(podService.getPODByOrderId(4L)).thenReturn(new OrderPOD());

    expectedException.expect(DataException.class);
    expectedException.expectMessage("error.restapi.delivery.already.confirmed");

    restPODService.updatePOD(spyOrderPod, 1L);
  }

  @Test
  public void shouldThrowErrorWhenInvalidProductCodes() throws Exception {
    OrderPOD orderPod = new OrderPOD(3L);
    orderPod.setOrderNumber("ON123");

    OrderPOD spyOrderPod = spy(orderPod);
    doNothing().when(spyOrderPod).validate();
    when(orderService.getByOrderNumber("ON123")).thenReturn(new Order(4L));

    when(podService.getPODByOrderId(4L)).thenReturn(null);

    Rnr rnr = new Rnr();
    when(requisitionService.getLWById(4L)).thenReturn(rnr);
    doNothing().when(spyOrderPod).fillPOD(rnr);
    doNothing().when(podService).insertPOD(orderPod);

    OrderPODLineItem orderPODLineItem = mock(OrderPODLineItem.class);
    when(orderPODLineItem.getProductCode()).thenReturn("ABC");
    when(productService.getByCode("ABC")).thenReturn(null);
    when(spyOrderPod.getPodLineItems()).thenReturn(asList(orderPODLineItem));

    expectedException.expect(dataExceptionMatcher("error.invalid.product.code"));

    restPODService.updatePOD(spyOrderPod, 1L);
  }
}
