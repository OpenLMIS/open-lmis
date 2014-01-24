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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.*;

@Category(IntegrationTests.class)
@RunWith(MockitoJUnitRunner.class)
public class FulfillmentPermissionServiceTest {

  @Mock
  private RoleRightsService roleRightsService;

  @Mock
  private OrderService orderService;

  @InjectMocks
  private FulfillmentPermissionService fulfillmentPermissionService;

  @Test
  public void shouldReturnTrueIfUserHasGivenRightOnAWarehouse() {
    Set<Right> rights = new HashSet<Right>() {{
      add(MANAGE_POD);
      add(FACILITY_FILL_SHIPMENT);
    }};
    long userId = 1L;
    long orderId = 2L;
    long facilityID = 12345L;
    when(roleRightsService.getRightsForUserAndWarehouse(userId, facilityID)).thenReturn(rights);
    Order order = mock(Order.class);
    when(order.getSupplyingFacility()).thenReturn(new Facility(facilityID));
    when(orderService.getOrder(2l)).thenReturn(order);

    Boolean hasRight = fulfillmentPermissionService.hasPermission(userId, orderId, MANAGE_POD);

    assertThat(hasRight, is(true));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveGivenRightOnAWarehouse() {
    Set<Right> rights = new HashSet<Right>() {{
      add(VIEW_ORDER);
      add(FACILITY_FILL_SHIPMENT);
    }};
    long userId = 1l;
    long orderId = 2L;
    long facilityID = 12345L;

    when(roleRightsService.getRightsForUserAndWarehouse(userId, orderId)).thenReturn(rights);
    Order order = mock(Order.class);
    when(order.getSupplyingFacility()).thenReturn(new Facility(facilityID));
    when(orderService.getOrder(orderId)).thenReturn(order);

    Boolean hasRight = fulfillmentPermissionService.hasPermission(userId, orderId, MANAGE_POD);

    assertThat(hasRight, is(false));
  }
}
