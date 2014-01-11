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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.service.PODService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.PODController.ORDER_POD;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class PODControllerTest {

  private static final Long USER_ID = 1L;
  private MockHttpServletRequest request;
  private static final String USER = "user";

  @Mock
  private PODService service;

  @InjectMocks
  private PODController controller;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);
  }

  @Test
  public void shouldGetOrderPODGivenAnOrderId() throws Exception {
    Long orderId = 1L;

    OrderPOD orderPOD = new OrderPOD();
    when(service.getPOD(orderId, USER_ID)).thenReturn(orderPOD);
    ResponseEntity<OpenLmisResponse> response = controller.updatePOD(request, orderId);

    verify(service).getPOD(orderId, USER_ID);
    assertThat((OrderPOD) response.getBody().getData().get(ORDER_POD), is(orderPOD));
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }
}
