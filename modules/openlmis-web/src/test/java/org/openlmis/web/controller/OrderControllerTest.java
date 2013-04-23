package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderDTO;
import org.openlmis.order.service.OrderService;
import org.openlmis.web.form.RequisitionList;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.OrderController.ORDERS;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderDTO.class)
public class OrderControllerTest {

  private static final Integer USER_ID = 1;
  private MockHttpServletRequest request;
  private static final String USER = "user";

  @Mock
  private OrderService orderService;

  @SuppressWarnings("unused")
  @InjectMocks
  private OrderController orderController;

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    RequisitionList rnrList = new RequisitionList();
    orderController.convertToOrder(rnrList, request);
    verify(orderService).convertToOrder(rnrList, 1);
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
}
