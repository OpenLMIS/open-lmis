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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
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
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.service.DonorService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DonorControllerTest {

  @Rule
  public PowerMockRule rule = new PowerMockRule();

  @Mock
  DonorService donorService;

  @InjectMocks
  DonorController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
  }


  @Test
  public void shouldGetAll() throws Exception {
    List<Donor> list = asList(new Donor());
    when(donorService.getAllWithDetails()).thenReturn(list);
    ResponseEntity<OpenLmisResponse> response = controller.getAll();
    assertThat(list, is(response.getBody().getData().get("donors")));
  }

  @Test
  public void shouldSave() throws Exception {
    Donor donor = makeADonor();
    doNothing().when(donorService).save(donor);
    when(donorService.getById(donor.getId())).thenReturn(donor);
    ResponseEntity<OpenLmisResponse> response = controller.save(donor, request);
    assertThat(donor, is(response.getBody().getData().get("donor")));
  }

  private Donor makeADonor() {
    Donor donor = new Donor();
    donor.setId(25L);
    donor.setCode("donor-1");
    donor.setLongName("Donor One");
    return donor;
  }

  @Test
  public void shouldGetDetailsForDonor() throws Exception {
    Donor donor = makeADonor();
    when(donorService.getById(25L)).thenReturn(donor);
    ResponseEntity<OpenLmisResponse> response = controller.getDetailsForDonor(25L);
    assertThat(donor, is(response.getBody().getData().get("donor")));
  }

  @Test
  public void shouldRemove() throws Exception {
    doNothing().when(donorService).removeDonor(20L);
    ResponseEntity<OpenLmisResponse> response = controller.remove(20L, request);
    assertThat(response.getBody().getSuccessMsg(), is(notNullValue()));
  }
}