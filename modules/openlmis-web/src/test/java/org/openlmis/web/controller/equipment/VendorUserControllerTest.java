/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.equipment;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.service.VendorUserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorUserControllerTest {

  @Mock
  VendorUserService service;

  @Mock
  MessageService messageService;

  @InjectMocks
  VendorUserController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
    when(messageService.message(anyString())).thenReturn("the message");
  }

  @Test
  public void shouldGetAllUsersForVendor() throws Exception {
    List<User> users = new ArrayList<>();
    when(service.getAllUsersForVendor(2L)).thenReturn(users);
    ResponseEntity<OpenLmisResponse> response = controller.getAllUsersForVendor(2L);
    assertThat(users, is(response.getBody().getData().get("users")));
  }

  @Test
  public void shouldGetAllUsersAvailableForVendor() throws Exception {
    List<User> users = new ArrayList<>();
    when(service.getAllUsersAvailableForVendor()).thenReturn(users);
    ResponseEntity<OpenLmisResponse> response = controller.getAllUsersAvailableForVendor();
    assertThat(users, is(response.getBody().getData().get("users")));
  }

  @Test
  public void shouldSaveVendorUsers() throws Exception {
    VendorUser vu = new VendorUser();
    vu.setUser(new User(2L, "theUser"));
    vu.setVendor(new Vendor());
    ResponseEntity<OpenLmisResponse> response = controller.saveVendorUsers(vu, request);
    assertThat(vu, is(response.getBody().getData().get("vendorUser")));
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }

  @Test
  public void shouldRemove() throws Exception {
    doNothing().when(service).removeVendorUserAssociation(2L, 3L);
    ResponseEntity<OpenLmisResponse> response = controller.remove(2L, 3L);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }
}