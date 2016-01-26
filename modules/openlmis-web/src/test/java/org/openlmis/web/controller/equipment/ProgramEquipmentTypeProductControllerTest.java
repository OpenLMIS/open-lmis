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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Product;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.EquipmentTypeProduct;
import org.openlmis.equipment.service.ProgramEquipmentTypeProductService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ProgramEquipmentTypeProductControllerTest {

  @Mock
  ProgramEquipmentTypeProductService service;

  @InjectMocks
  ProgramEquipmentTypeProductController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
  }


  @Test
  public void shouldGetByProgramEquipmentId() throws Exception {
    List<EquipmentTypeProduct> list = new ArrayList<>();
    when(service.getByProgramEquipmentId(2L)).thenReturn(list);

    ResponseEntity<OpenLmisResponse> response = controller.getByProgramEquipmentId(2L);
    assertThat(list,is(response.getBody().getData().get("programEquipmentProducts")));
  }

  @Test
  public void shouldSave() throws Exception {
    EquipmentTypeProduct pep = new EquipmentTypeProduct();
    doNothing().when(service).Save(pep);

    ResponseEntity<OpenLmisResponse> response = controller.save(pep, request);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
    assertThat(pep, is(response.getBody().getData().get("programEquipmentProduct")));
  }

  @Test
  public void shouldRemove() throws Exception {

    doNothing().when(service).remove(2L);
    ResponseEntity<OpenLmisResponse> response = controller.remove(2L);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }

  @Test
  public void shouldGetProducts() throws Exception {
    List<Product> products = new ArrayList<>();
    when(service.getAvailableProductsToLink(2L, 2L)).thenReturn(products);

    ResponseEntity<OpenLmisResponse> response = controller.getProducts(2L, 2L);
    assertThat(products, is(response.getBody().getData().get("products")));
  }
}