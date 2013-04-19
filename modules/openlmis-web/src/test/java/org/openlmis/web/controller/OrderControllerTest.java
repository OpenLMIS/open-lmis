package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.web.form.RnrList;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

  private static final Integer USER_ID = 1;
  private MockHttpServletRequest request;
  private static final String USER = "user";

  @Mock
  private OrderService orderService;
  @InjectMocks
  private OrderController orderController;

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);

    List<RnrDTO> rnrDTOs = new ArrayList<>();
    RnrList rnrList = new RnrList();
    rnrList.setRnrList(rnrDTOs);
    orderController.convertToOrder(rnrList, request);
    verify(orderService).convertToOrder(rnrDTOs, 1);
  }
}
