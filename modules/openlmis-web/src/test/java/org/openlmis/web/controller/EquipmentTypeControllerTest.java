/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.service.EquipmentTypeService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class EquipmentTypeControllerTest {

  @Mock
  EquipmentTypeService service;

  @InjectMocks
  EquipmentTypeController controller;

  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  @Before
  public void setUp() throws Exception {
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER, USER);
    mockHttpSession.setAttribute(USER_ID, 1L);
  }

  @Test
  public void shouldGetAll() throws Exception {
    controller.getAll();
    verify(service).getAll();
  }

  @Test
  public void shouldGetById() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test");
    when(service.getTypeById(1L)).thenReturn(type);

    controller.getById(1L);
    verify(service).getTypeById(1L);
  }

  @Test
  public void shouldSave() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test");

    controller.save(type);
    verify(service).save(type);
  }
}