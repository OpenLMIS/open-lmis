/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.RightName;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.DateFormat;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderDTO;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.service.OrderService;
import org.openlmis.web.form.RequisitionList;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.domain.RightName.MANAGE_POD;
import static org.openlmis.order.domain.OrderStatus.*;
import static org.openlmis.web.controller.OrderController.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(OrderDTO.class)
public class OrderControllerTest {

  private static final Long USER_ID = 1L;
  private MockHttpServletRequest request;
  private static final String USER = "user";

  @Mock
  private OrderService orderService;

  @SuppressWarnings("unused")
  @InjectMocks
  private OrderController orderController;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);
  }

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    RequisitionList rnrList = new RequisitionList();
    ResponseEntity<OpenLmisResponse> response = orderController.convertToOrder(rnrList, request);
    assertThat(response.getStatusCode(), is(CREATED));
    verify(orderService).convertToOrder(rnrList, 1L);
  }

  @Test
  public void shouldReturnErrorMsgIfSomeRequisitionsAreAlreadyConverted() {
    RequisitionList rnrList = new RequisitionList();
    doThrow(new DataException("msg.rnr.already.converted.to.order")).
      when(orderService).convertToOrder(rnrList, 1L);
    ResponseEntity<OpenLmisResponse> response = orderController.convertToOrder(rnrList, request);
    assertThat(response.getStatusCode(), is(CONFLICT));
    assertThat(response.getBody().getErrorMsg(), is("msg.rnr.already.converted.to.order"));
    verify(orderService).convertToOrder(rnrList, 1L);
  }

  @Test
  public void shouldReturnAllOrdersForPage() throws Exception {
    List<Order> orders = new ArrayList<Order>() {{
      new Order();
    }};

    mockStatic(OrderDTO.class);
    when(orderService.getOrdersForPage(2, USER_ID, RightName.VIEW_ORDER)).thenReturn(orders);
    List<OrderDTO> orderDTOs = new ArrayList<>();
    when(OrderDTO.getOrdersForView(orders)).thenReturn(orderDTOs);

    ResponseEntity<OpenLmisResponse> fetchedOrders = orderController.getOrdersForPage(2,0L,0L,0L, request);

    verify(orderService).getOrdersForPage(2, USER_ID, RightName.VIEW_ORDER);
    assertThat((List<OrderDTO>) fetchedOrders.getBody().getData().get(ORDERS), is(orderDTOs));
  }

  @Test
  public void shouldAddPageInfoForOrders() throws Exception {
    when(orderService.getPageSize()).thenReturn(3);

    ResponseEntity<OpenLmisResponse> fetchedOrders = orderController.getOrdersForPage(2,0L,0L,0L, request);

    assertThat((Integer) fetchedOrders.getBody().getData().get("pageSize"), is(3));
  }

  @Test
  public void shouldAddTotalNumberOfPagesForOrders() throws Exception {
    when(orderService.getNumberOfPages()).thenReturn(5);

    ResponseEntity<OpenLmisResponse> fetchedOrders = orderController.getOrdersForPage(2,0L,0L,0L, request);

    assertThat((Integer) fetchedOrders.getBody().getData().get("numberOfPages"), is(5));
  }

  @Test
  public void shouldDownloadOrderCsv() {
    Long orderId = 1L;
    Order expectedOrder = new Order();
    when(orderService.getOrder(orderId)).thenReturn(expectedOrder);
    OrderFileTemplateDTO expectedOrderFileTemplateDTO = new OrderFileTemplateDTO(new OrderConfiguration(),
      new ArrayList<OrderFileColumn>());
    when(orderService.getOrderFileTemplateDTO()).thenReturn(expectedOrderFileTemplateDTO);
    ModelAndView modelAndView = orderController.downloadOrderCsv(orderId);
    Order order = (Order) modelAndView.getModel().get(ORDER);
    OrderFileTemplateDTO orderFileTemplate = (OrderFileTemplateDTO) modelAndView.getModel().get(ORDER_FILE_TEMPLATE);
    assertThat(order, is(expectedOrder));
    assertThat(orderFileTemplate, is(expectedOrderFileTemplateDTO));
    verify(orderService).getOrder(orderId);
    verify(orderService).getOrderFileTemplateDTO();
  }

  @Test
  public void shouldGetOrderFileTemplateDTO() throws Exception {
    OrderFileTemplateDTO expectedOrderFileTemplateDTO = new OrderFileTemplateDTO(new OrderConfiguration(),
      new ArrayList<OrderFileColumn>());
    when(orderService.getOrderFileTemplateDTO()).thenReturn(expectedOrderFileTemplateDTO);
    ResponseEntity<OpenLmisResponse> fetchedTemplate = orderController.getOrderFileTemplateDTO();
    verify(orderService).getOrderFileTemplateDTO();
    assertThat((OrderFileTemplateDTO) fetchedTemplate.getBody().getData().get(ORDER_FILE_TEMPLATE),
      is(expectedOrderFileTemplateDTO));
  }

  @Test
  public void shouldSaveOrderFileTemplateDTO() throws Exception {
    OrderFileTemplateDTO orderFileTemplateDTO = new OrderFileTemplateDTO(new OrderConfiguration(),
      new ArrayList<OrderFileColumn>());
    ResponseEntity<OpenLmisResponse> response = orderController.saveOrderFileTemplateDTO(orderFileTemplateDTO, request);
    verify(orderService).saveOrderFileTemplate(orderFileTemplateDTO, 1L);
    assertThat(response.getBody().getSuccessMsg(), is("order.file.template.saved.success"));
  }

  @Test
  public void shouldGetAllDateFormats() throws Exception {
    Set<DateFormat> dateFormats = new HashSet<>();
    when(orderService.getAllDateFormats()).thenReturn(dateFormats);
    ResponseEntity<OpenLmisResponse> response = orderController.getAllDateFormats();
    verify(orderService).getAllDateFormats();
    assertThat((Set<DateFormat>) response.getBody().getData().get(DATE_FORMATS), is(dateFormats));
  }

  @Test
  public void shouldGetAllOrdersForPOD() {
    List<Order> ordersForPOD = null;
    when(orderService.searchByStatusAndRight(USER_ID,
      MANAGE_POD,
      asList(RELEASED, PACKED, TRANSFER_FAILED, READY_TO_PACK),1L,0L)).thenReturn(ordersForPOD);
    mockStatic(OrderDTO.class);
    List<OrderDTO> orderDTOs = new ArrayList<>();
    when(OrderDTO.getOrdersForView(ordersForPOD)).thenReturn(orderDTOs);

    ResponseEntity<OpenLmisResponse> response = orderController.getOrdersForPOD(1L,0L,request);

    assertThat((List<OrderDTO>) response.getBody().getData().get(ORDERS_FOR_POD), is(orderDTOs));
    verify(orderService).searchByStatusAndRight(USER_ID,
      MANAGE_POD,
      asList(RELEASED, PACKED, TRANSFER_FAILED, READY_TO_PACK),1L,0L);
  }
}
