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
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.service.ServiceContractService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ServiceContractControllerTest {

  @Mock
  ServiceContractService service;

  @Mock
  MessageService messageService;

  @InjectMocks
  ServiceContractController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
    when(messageService.message(anyString())).thenReturn("message");
  }


  @Test
  public void shouldGetAll() throws Exception {
    List<ServiceContract> list = new ArrayList<>();
    when(service.getAll()).thenReturn(list);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();
    assertThat(list, is(response.getBody().getData().get("contracts")));
  }

  @Test
  public void shouldGetById() throws Exception {
    ServiceContract contract = new ServiceContract();
    when(service.getById(5L)).thenReturn(contract);

    ResponseEntity<OpenLmisResponse> response = controller.getById(5L);
    assertThat(contract, is(response.getBody().getData().get("contract")));
  }

  @Test
  public void shouldGetByFacilityId() throws Exception {
    List<ServiceContract> list = new ArrayList<>();
    when(service.getAllForFacility(2L)).thenReturn(list);

    ResponseEntity<OpenLmisResponse> response = controller.getByFacilityId(2L);
    assertThat(list, is(response.getBody().getData().get("contracts")));
  }

  @Test
  public void shouldGetByVendorId() throws Exception {
    List<ServiceContract> list = new ArrayList<>();
    when(service.getAllForVendor(5L)).thenReturn(list);

    ResponseEntity<OpenLmisResponse> response = controller.getByVendorId(5L);
    assertThat(list, is(response.getBody().getData().get("contracts")));
  }

  @Test
  public void shouldSave() throws Exception {
    ServiceContract contract = new ServiceContract();
    doNothing().when(service).save(contract);

    ResponseEntity<OpenLmisResponse> response = controller.save(contract);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
    assertThat(contract, is(response.getBody().getData().get("contract")));
  }
}