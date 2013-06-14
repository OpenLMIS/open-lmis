/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.openlmis.allocation.repository.DeliveryZoneWarehouseRepository;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.allocation.builder.DeliveryZoneBuilder.defaultDeliveryZone;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneWarehouseServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private DeliveryZoneWarehouseService service;

  @Mock
  private DeliveryZoneWarehouseRepository repository;

  @Mock
  private FacilityService facilityService;

  @Mock
  private DeliveryZoneService deliveryZoneService;

  private DeliveryZoneWarehouse deliveryZoneWarehouse;

  @Before
  public void setUp() throws Exception {
    deliveryZoneWarehouse = new DeliveryZoneWarehouse();
    deliveryZoneWarehouse.setWarehouse(make(a(FacilityBuilder.defaultFacility)));
    deliveryZoneWarehouse.setDeliveryZone(make(a(defaultDeliveryZone)));
    when(facilityService.getByCode(deliveryZoneWarehouse.getWarehouse())).thenReturn(deliveryZoneWarehouse.getWarehouse());
    when(deliveryZoneService.getByCode(deliveryZoneWarehouse.getDeliveryZone().getCode())).thenReturn(deliveryZoneWarehouse.getDeliveryZone());
  }

  @Test
  public void shouldInsertDeliveryZoneWarehouse() throws Exception {
    service.save(deliveryZoneWarehouse);
    verify(repository).insert(deliveryZoneWarehouse);
  }

  @Test
  public void shouldUpdateDeliveryZoneWarehouseIfIdExists() throws Exception {
    deliveryZoneWarehouse.setId(1L);
    service.save(deliveryZoneWarehouse);
    verify(repository).update(deliveryZoneWarehouse);
  }

  @Test
  public void shouldThrowErrorIfInvalidFacilityCode() throws Exception {
    when(facilityService.getByCode(deliveryZoneWarehouse.getWarehouse())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("warehouse.code.invalid");

    service.save(deliveryZoneWarehouse);
  }

  @Test
  public void shouldThrowErrorIfInvalidDZCode() throws Exception {
    when(deliveryZoneService.getByCode(deliveryZoneWarehouse.getDeliveryZone().getCode())).thenReturn(null);

    expectedException.expect(DataException.class);
    expectedException.expectMessage("deliveryZone.code.invalid");

    service.save(deliveryZoneWarehouse);
  }
}
