/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.fulfillment.shared;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.IntegrationTests;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.FACILITY_FILL_SHIPMENT;
import static org.openlmis.core.domain.Right.MANAGE_POD;
import static org.openlmis.core.domain.Right.VIEW_ORDER;

@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FulfillmentPermissionServiceTest {

  @Mock
  private RoleRightsService roleRightsService;

  @InjectMocks
  private FulfillmentPermissionService fulfillmentPermissionService;

  @Test
  public void shouldReturnTrueIfUserHasGivenRightOnAWarehouse() {
    Set<Right> rights = new HashSet<Right>() {{
      add(MANAGE_POD);
      add(FACILITY_FILL_SHIPMENT);
    }};
    when(roleRightsService.getRightsForUserAndWarehouse(1l, 2l)).thenReturn(rights);

    Boolean hasRight = fulfillmentPermissionService.hasPermission(1l, 2l, MANAGE_POD);

    assertThat(hasRight, is(true));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveGivenRightOnAWarehouse() {
    Set<Right> rights = new HashSet<Right>() {{
      add(VIEW_ORDER);
      add(FACILITY_FILL_SHIPMENT);
    }};
    when(roleRightsService.getRightsForUserAndWarehouse(1l, 2l)).thenReturn(rights);

    Boolean hasRight = fulfillmentPermissionService.hasPermission(1l, 2l, MANAGE_POD);

    assertThat(hasRight, is(false));
  }
}
