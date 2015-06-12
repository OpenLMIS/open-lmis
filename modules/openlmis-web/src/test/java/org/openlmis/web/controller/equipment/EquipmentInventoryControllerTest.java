/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.service.EquipmentInventoryService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class EquipmentInventoryControllerTest {

  @Mock
  MessageService messageService;

  @Mock
  EquipmentInventoryService service;

  @InjectMocks
  EquipmentInventoryController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
    when(messageService.message(anyString())).thenReturn("the message");
  }


  @Test
  public void shouldGetInventory() throws Exception {
    List<EquipmentInventory> inventories = new ArrayList<>();
    Pagination page = new Pagination(1, 2);
    when(service.getInventory(1L, 1L, 1L, 1L, page)).thenReturn(inventories);

    ResponseEntity<OpenLmisResponse> response = controller.getInventory(1L, 1L, 1L, 1, "2", request);
    assertThat(inventories, is(response.getBody().getData().get("inventory")));
  }

  @Test
  public void shouldGetInventoryById() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    when(service.getInventoryById(1L)).thenReturn(inventory);
    ResponseEntity<OpenLmisResponse> response = controller.getInventory(1L);
    assertThat(inventory, is(response.getBody().getData().get("inventory")));
  }

  @Test
  public void shouldSave() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setId(32L);
    doNothing().when(service).save(inventory);

    ResponseEntity<OpenLmisResponse> response = controller.save(inventory, request);
    assertThat(inventory, is(response.getBody().getData().get("inventory")));
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }
}