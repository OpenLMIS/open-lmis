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
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.service.EquipmentService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

public class EquipmentControllerTest {

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Mock
  EquipmentService service;

  @InjectMocks
  EquipmentController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
  }


  @Test
  public void shouldGetEquipmentById() throws Exception {
    Equipment equipment = makeAnEquipment();
    when(service.getById(2L)).thenReturn(equipment);
    ResponseEntity<OpenLmisResponse> response = controller.getEquipmentById(2L);
    assertThat(equipment, is(response.getBody().getData().get("equipment")));
  }

  private Equipment makeAnEquipment() {
    Equipment equipment = new Equipment();
    equipment.setId(2L);
    equipment.setName("Ice Maker");
    return equipment;
  }

  @Test
  public void shouldGetList() throws Exception {
    Equipment equipment = makeAnEquipment();
    when(service.getAll()).thenReturn(asList(equipment));

    ResponseEntity<OpenLmisResponse> response = controller.getList();
    assertThat(asList(equipment), is(response.getBody().getData().get("equipments")));
  }



  @Test
  public void shouldSaveChanges() throws Exception {
    doNothing().when(service).save(any(Equipment.class));
    Equipment equipment = makeAnEquipment();
    ResponseEntity<OpenLmisResponse> response = controller.save(equipment);

    assertThat(equipment, is(response.getBody().getData().get("equipment")));
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }
}