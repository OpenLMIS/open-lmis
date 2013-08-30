package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.DateFormat;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.dto.OrderDTO;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.service.OrderService;
import org.openlmis.web.form.RequisitionList;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.OrderController.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderDTO.class)
@Category(UnitTests.class)
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
    orderController.convertToOrder(rnrList, request);
    verify(orderService).convertToOrder(rnrList, 1L);
  }

  @Test
  public void shouldReturnAllOrders() throws Exception {
    List<Order> orders = new ArrayList<Order>() {{
      new Order();
    }};
    mockStatic(OrderDTO.class);
    when(orderService.getOrders()).thenReturn(orders);
    List<OrderDTO> orderDTOs = new ArrayList<>();
    when(OrderDTO.getOrdersForView(orders)).thenReturn(orderDTOs);

    ResponseEntity<OpenLmisResponse> fetchedOrders = orderController.getOrders();

    verify(orderService).getOrders();
    assertThat((List<OrderDTO>) fetchedOrders.getBody().getData().get(ORDERS), is(orderDTOs));
  }

  @Test
  public void shouldDownloadOrderCsv() {
    Long orderId = 1L;
    Order expectedOrder = new Order();
    when(orderService.getOrderForDownload(orderId)).thenReturn(expectedOrder);
    OrderFileTemplateDTO expectedOrderFileTemplateDTO =
      new OrderFileTemplateDTO(new OrderConfiguration(), new ArrayList<OrderFileColumn>());
    when(orderService.getOrderFileTemplateDTO()).thenReturn(expectedOrderFileTemplateDTO);
    ModelAndView modelAndView = orderController.downloadOrderCsv(orderId);
    Order order = (Order) modelAndView.getModel().get(ORDER);
    OrderFileTemplateDTO orderFileTemplate = (OrderFileTemplateDTO) modelAndView.getModel().get(ORDER_FILE_TEMPLATE);
    assertThat(order, is(expectedOrder));
    assertThat(orderFileTemplate, is(expectedOrderFileTemplateDTO));
    verify(orderService).getOrderForDownload(orderId);
    verify(orderService).getOrderFileTemplateDTO();
  }

  @Test
  public void shouldGetOrderFileTemplateDTO() throws Exception {
    OrderFileTemplateDTO expectedOrderFileTemplateDTO =
      new OrderFileTemplateDTO(new OrderConfiguration(), new ArrayList<OrderFileColumn>());
    when(orderService.getOrderFileTemplateDTO()).thenReturn(expectedOrderFileTemplateDTO);
    ResponseEntity<OpenLmisResponse> fetchedTemplate = orderController.getOrderFileTemplateDTO();
    verify(orderService).getOrderFileTemplateDTO();
    assertThat((OrderFileTemplateDTO) fetchedTemplate.getBody().getData().get(ORDER_FILE_TEMPLATE), is(expectedOrderFileTemplateDTO));
  }

  @Test
  public void shouldSaveOrderFileTemplateDTO() throws Exception {
    OrderFileTemplateDTO orderFileTemplateDTO = new OrderFileTemplateDTO(new OrderConfiguration(), new ArrayList<OrderFileColumn>());
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
}
