package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.web.form.RequisitionList;
import org.openlmis.web.response.OpenLmisResponse;
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

@RunWith(MockitoJUnitRunner.class)
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
  public void shouldReturnAllFilledOrders() throws Exception {
    List<Order> orderedRequisitions = new ArrayList<>();
    mockStatic(RnrDTO.class);
    when(orderService.getOrders()).thenReturn(orderedRequisitions);

    ResponseEntity<OpenLmisResponse> fetchedOrders = orderController.getOrders();

    verify(orderService).getOrders();
    assertThat((List<Order>) fetchedOrders.getBody().getData().get(ORDERS), is(orderedRequisitions));
  }
}
