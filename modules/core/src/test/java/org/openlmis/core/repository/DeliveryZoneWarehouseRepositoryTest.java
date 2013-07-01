/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.openlmis.core.repository.DeliveryZoneWarehouseRepository;
import org.openlmis.core.repository.mapper.DeliveryZoneWarehouseMapper;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneWarehouseRepositoryTest {

  @Mock
  private DeliveryZoneWarehouseMapper mapper;

  @InjectMocks
  private DeliveryZoneWarehouseRepository repository;

  @Test
  public void shouldInsertDeliveryZoneWarehouse() throws Exception {
    DeliveryZoneWarehouse warehouse = new DeliveryZoneWarehouse();
    repository.insert(warehouse);
    verify(mapper).insert(warehouse);
  }

  @Test
  public void shouldUpdateDeliveryZoneWarehouse() throws Exception {
    DeliveryZoneWarehouse warehouse = new DeliveryZoneWarehouse();
    repository.update(warehouse);
    verify(mapper).update(warehouse);
  }
}
