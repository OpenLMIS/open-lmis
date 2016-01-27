/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.dto.OrderPODDTO;
import org.openlmis.pod.service.PODService;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.PODController.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({OrderPODDTO.class, PODController.class})
public class PODControllerTest {

  private static final Long USER_ID = 1L;
  private MockHttpServletRequest request;
  private static final String USER = "user";

  @Mock
  private PODService service;

  @Mock
  private OrderService orderService;

  @Mock
  private TemplateService templateService;

  @Mock
  private JasperReportsViewFactory jasperReportsViewFactory;

  @InjectMocks
  private PODController controller;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);
  }

  @Test
  public void shouldCreateOrderPODGivenAnOrderId() throws Exception {
    Long orderId = 1L;

    OrderPOD orderPOD = new OrderPOD();
    OrderPOD createdPOD = new OrderPOD();
    mockStatic(OrderPODDTO.class);

    whenNew(OrderPOD.class).withArguments(orderId, "OrdNum", USER_ID).thenReturn(orderPOD);
    when(service.getPODByOrderId(orderId)).thenReturn(null);
    when(service.createPOD(orderPOD)).thenReturn(createdPOD);

    Order order = new Order(orderId);
    order.setOrderNumber("OrdNum");
    OrderPODDTO orderPODDTO = mock(OrderPODDTO.class);
    when(orderService.getOrder(orderId)).thenReturn(order);
    when(OrderPODDTO.getOrderDetailsForPOD(order)).thenReturn(orderPODDTO);

    ResponseEntity<OpenLmisResponse> response = controller.createPOD(orderId, request);

    verify(service).createPOD(orderPOD);
    assertThat((OrderPOD) response.getBody().getData().get(ORDER_POD), is(orderPOD));
    assertThat((OrderPODDTO) response.getBody().getData().get(ORDER), is(orderPODDTO));
    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  public void shouldGetPODIfAlreadyExistsForOrder() throws Exception {
    Long orderId = 2L;
    OrderPOD existingPOD = new OrderPOD();
    mockStatic(OrderPODDTO.class);
    Order order = new Order(orderId);
    OrderPODDTO orderPODDTO = mock(OrderPODDTO.class);
    when(orderService.getOrder(orderId)).thenReturn(order);
    when(OrderPODDTO.getOrderDetailsForPOD(order)).thenReturn(orderPODDTO);
    when(service.getPODByOrderId(2L)).thenReturn(existingPOD);

    ResponseEntity<OpenLmisResponse> response = controller.createPOD(2L, request);

    assertThat((OrderPOD) response.getBody().getData().get(PODController.ORDER_POD), is(existingPOD));
    assertThat((OrderPODDTO) response.getBody().getData().get(ORDER), is(orderPODDTO));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldGetOrderPODByPodId() throws Exception {
    Long podId = 1L;
    Long orderId = 2L;

    OrderPOD orderPOD = new OrderPOD();
    orderPOD.setOrderId(orderId);
    OrderPOD spyOrderPOD = spy(orderPOD);
    mockStatic(OrderPODDTO.class);

    when(service.getPodById(podId)).thenReturn(spyOrderPOD);
    when(spyOrderPOD.getStringReceivedDate()).thenReturn("2014-02-10");

    Order order = new Order();
    when(orderService.getOrder(orderId)).thenReturn(order);

    OrderPODDTO orderPODDTO = mock(OrderPODDTO.class);
    when(OrderPODDTO.getOrderDetailsForPOD(order)).thenReturn(orderPODDTO);

    ResponseEntity<OpenLmisResponse> response = controller.getPOD(podId);

    verify(service).getPodById(podId);
    assertThat((OrderPOD) response.getBody().getData().get(ORDER_POD), is(spyOrderPOD));
    assertThat((OrderPODDTO) response.getBody().getData().get(ORDER), is(orderPODDTO));
    assertThat((String) response.getBody().getData().get(RECEIVED_DATE), is("2014-02-10"));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

  @Test
  public void shouldSavePOD() throws Exception {
    OrderPOD orderPOD = new OrderPOD();

    ResponseEntity<OpenLmisResponse> response = controller.save(orderPOD, 4L, request);

    verify(service).save(orderPOD);
    assertThat(response.getBody().getSuccessMsg(), is("msg.pod.save.success"));
    assertThat(orderPOD.getId(), is(4L));
    assertThat(orderPOD.getModifiedBy(), is(USER_ID));
  }

  @Test
  public void shouldGiveErrorResponseIfSaveUnsuccessful() throws Exception {
    OrderPOD orderPOD = new OrderPOD();

    doThrow(new DataException("error")).when(service).save(orderPOD);

    ResponseEntity<OpenLmisResponse> response = controller.save(orderPOD, 5L, request);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("error"));
  }

  @Test
  public void shouldSubmitPOD() throws Exception {
    Long podId = 4L;
    OrderPOD orderPOD = new OrderPOD();
    orderPOD.setId(podId);
    when(service.submit(podId, USER_ID)).thenReturn(orderPOD);
    ResponseEntity<OpenLmisResponse> response = controller.submit(podId, request);

    verify(service).submit(podId, USER_ID);
    assertThat(response.getBody().getSuccessMsg(), is("msg.pod.submit.success"));
  }

  @Test
  public void shouldReturnErrorIfCouldNotSubmitPOD() throws Exception {
    Long podId = 1234L;
    doThrow(new DataException("msg.pod.submit.failure")).when(service).submit(podId, USER_ID);

    ResponseEntity<OpenLmisResponse> response = controller.submit(podId, request);

    assertThat(response.getBody().getErrorMsg(), is("msg.pod.submit.failure"));
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }

}
