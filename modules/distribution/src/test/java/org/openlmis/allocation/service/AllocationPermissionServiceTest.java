/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.MANAGE_DISTRIBUTION;

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
