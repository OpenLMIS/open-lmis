/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ShipmentRoleAssignment;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.ShipmentRoleRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentRoleServiceTest {

  @Mock
  ShipmentRoleRepository shipmentRoleRepository;

  @InjectMocks
  ShipmentRoleService shipmentRoleService;

  @Test
  public void shouldGetShipmentRolesForUser() throws Exception {
    List<ShipmentRoleAssignment> shipmentRoleAssignments = asList(new ShipmentRoleAssignment());
    Long userId = 1L;
    Mockito.when(shipmentRoleRepository.getShipmentRolesForUser(userId)).thenReturn(shipmentRoleAssignments);

    List<ShipmentRoleAssignment> expectedShipmentRoleAssignments = shipmentRoleService.getRolesForUser(userId);

    verify(shipmentRoleRepository).getShipmentRolesForUser(userId);

    assertThat(expectedShipmentRoleAssignments, is(shipmentRoleAssignments));
  }

  @Test
  public void shouldSaveShipmentRolesForUser() throws Exception {
    List<ShipmentRoleAssignment> shipmentRoleAssignments = asList(new ShipmentRoleAssignment());
    User user = new User();

    user.setShipmentRoles(shipmentRoleAssignments);
    shipmentRoleService.saveShipmentRoles(user);

    verify(shipmentRoleRepository).insertShipmentRoles(user);
  }
}
