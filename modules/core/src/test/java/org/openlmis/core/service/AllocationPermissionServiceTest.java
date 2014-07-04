/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class AllocationPermissionServiceTest {

  @InjectMocks
  AllocationPermissionService permissionService;

  @Mock
  DeliveryZoneService zoneService;

  @Test
  public void shouldReturnTrueIfUserHasPermissionOnZone() throws Exception {
    List<DeliveryZone> zones = new ArrayList<DeliveryZone>() {{
      add(new DeliveryZone(2l));
    }};
    when(zoneService.getByUserForRight(1l, MANAGE_DISTRIBUTION)).thenReturn(zones);

    assertTrue(permissionService.hasPermissionOnZone(1l, 2l));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHavePermissionOnZone() throws Exception {
    List<DeliveryZone> zones = new ArrayList<>();
    when(zoneService.getByUserForRight(1l, MANAGE_DISTRIBUTION)).thenReturn(zones);

    assertFalse(permissionService.hasPermissionOnZone(1l, 2l));
  }
}
