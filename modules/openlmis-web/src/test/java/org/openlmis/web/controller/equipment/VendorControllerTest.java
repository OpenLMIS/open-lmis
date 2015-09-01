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
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Vendor;
import org.openlmis.equipment.service.VendorService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorControllerTest {

  @Mock
  VendorService service;

  @Mock
  MessageService messageService;

  @InjectMocks
  VendorController controller;


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
    List<Vendor> vendors = new ArrayList<>();
    when(service.getAll()).thenReturn(vendors);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();
    assertThat(vendors, is(response.getBody().getData().get("vendors")));
  }

  @Test
  public void shouldGetById() throws Exception {
    Vendor vendor = new Vendor();
    when(service.getById(2L)).thenReturn(vendor);

    ResponseEntity<OpenLmisResponse> response = controller.getById(2L);
    assertThat(vendor, is(response.getBody().getData().get("vendor")));
  }

  @Test
  public void shouldSave() throws Exception {
    Vendor vendor = new Vendor();
    doNothing().when(service).save(vendor);

    ResponseEntity<OpenLmisResponse> response = controller.save(vendor);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
    assertThat(vendor, is(response.getBody().getData().get("vendor")));
  }

  @Test
  public void shouldRemoveSuccessfully() throws Exception {
    doNothing().when(service).removeVendor(2L);
    ResponseEntity<OpenLmisResponse> response = controller.remove(2L);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }

  @Test
  public void shouldThrowErrorWhenRemoveUnsuccessful() throws Exception {
    doThrow(DataIntegrityViolationException.class).when(service).removeVendor(2L);
    ResponseEntity<OpenLmisResponse> response = controller.remove(2L);
    assertThat(response.getBody().getErrorMsg(), is(notNullValue()));
    assertThat(response.getBody().getSuccessMsg(), is(nullValue()));
  }
}