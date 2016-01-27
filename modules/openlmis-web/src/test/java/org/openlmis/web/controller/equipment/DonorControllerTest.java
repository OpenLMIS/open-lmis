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
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.service.DonorService;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class DonorControllerTest {

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