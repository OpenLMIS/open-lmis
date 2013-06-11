/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
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
import org.openlmis.allocation.repository.DeliveryZoneRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.PLAN_DISTRIBUTION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneServiceTest {

  @Mock
  DeliveryZoneRepository repository;

  @InjectMocks
  DeliveryZoneService service;

  @Test
  public void shouldInsertDeliveryZone() throws Exception {
    DeliveryZone zone = new DeliveryZone();

    service.save(zone);

    verify(repository).insert(zone);
  }

  @Test
  public void shouldUpdateDeliveryZoneIfIdAlreadyExists() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    zone.setId(1l);

    service.save(zone);

    verify(repository).update(zone);
  }

  @Test
  public void shouldGetDeliveryZoneByCode() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    when(repository.getByCode("code")).thenReturn(zone);

    DeliveryZone returnedZone = service.getByCode("code");

    verify(repository).getByCode("code");
    assertThat(returnedZone, is(zone));
  }

  @Test
  public void shouldGetAllDeliveryZonesForAUser() throws Exception {
    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(repository.getByUserForRight(1l, PLAN_DISTRIBUTION)).thenReturn(deliveryZones);

    List<DeliveryZone> returnedZones = service.getByUserForRight(1l, PLAN_DISTRIBUTION);

    verify(repository).getByUserForRight(1l, PLAN_DISTRIBUTION);
    assertThat(returnedZones, is(deliveryZones));
  }
}
