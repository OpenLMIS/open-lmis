/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionPermissionServiceTest {

  @Mock
  private DeliveryZoneService deliveryZoneService;

  @Mock
  private DistributionService distributionService;

  @InjectMocks
  DistributionPermissionService distributionPermissionService;

  @Test
  public void shouldReturnTrueIfUserHasPermissionOnADeliveryZone() {
    Long userId = 1L;
    Long distributionId = 2L;
    DeliveryZone deliveryZone = new DeliveryZone();
    deliveryZone.setCode("DZ");
    Distribution distribution = new Distribution();
    distribution.setDeliveryZone(deliveryZone);
    when(deliveryZoneService.getByUserForRight(userId, MANAGE_DISTRIBUTION)).thenReturn(asList(deliveryZone));
    when(distributionService.getBy(distributionId)).thenReturn(distribution);

    assertThat(distributionPermissionService.hasPermission(userId, "MANAGE_DISTRIBUTION", distributionId), is(true));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHasPermissionOnADeliveryZone() {
    Long userId = 1L;
    Long distributionId = 2L;
    List<DeliveryZone> deliveryZones = emptyList();
    when(deliveryZoneService.getByUserForRight(userId, MANAGE_DISTRIBUTION)).thenReturn(
      deliveryZones);
    when(distributionService.getBy(distributionId)).thenReturn(new Distribution());

    assertThat(distributionPermissionService.hasPermission(userId, "MANAGE_DISTRIBUTION", 1L), is(false));
  }
}
