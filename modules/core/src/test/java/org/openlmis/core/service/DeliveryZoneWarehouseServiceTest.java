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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.openlmis.core.repository.DeliveryZoneWarehouseRepository;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.DeliveryZoneWarehouseService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;

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
