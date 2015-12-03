/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.DeliveryZoneMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneRepositoryTest {

  @Mock
  DeliveryZoneMapper mapper;

  @InjectMocks
  DeliveryZoneRepository repository;

  @Test
  public void shouldInsertDeliveryZone() throws Exception {
    DeliveryZone zone = new DeliveryZone();

    repository.insert(zone);

    verify(mapper).insert(zone);
  }

  @Test
  public void shouldUpdateDeliveryZone() throws Exception {
    DeliveryZone zone = new DeliveryZone();

    repository.update(zone);

    verify(mapper).update(zone);
  }

  @Test
  public void shouldGetDeliveryZoneByCode() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    when(mapper.getByCode("code")).thenReturn(zone);

    DeliveryZone returnedZone = repository.getByCode("code");

    verify(mapper).getByCode("code");
    assertThat(returnedZone, is(zone));
  }

  @Test
  public void shouldGetAllDeliveryZonesForAUser() throws Exception {
    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(mapper.getByUserForRight(1l, MANAGE_DISTRIBUTION)).thenReturn(deliveryZones);

    List<DeliveryZone> returnedZones = repository.getByUserForRight(1l, MANAGE_DISTRIBUTION);

    verify(mapper).getByUserForRight(1l, MANAGE_DISTRIBUTION);
    assertThat(returnedZones, is(deliveryZones));
  }

  @Test
  public void shouldGetAllDeliveryZones() throws Exception {
    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(mapper.getAll()).thenReturn(deliveryZones);

    List<DeliveryZone> returnedZones = repository.getAll();

    verify(mapper).getAll();
    assertThat(returnedZones, is(deliveryZones));
  }

  @Test
  public void shouldGetProgramForDeliveryZoneBasedOnUserRights() throws Exception {
    List<Program> programs = new ArrayList<>();
    when(mapper.getPrograms(1l)).thenReturn(programs);

    List<Program> returnedPrograms = repository.getPrograms(1l);

    verify(mapper).getPrograms(1l);
    assertThat(returnedPrograms, is(programs));
  }

  @Test
  public void shouldGetDeliveryZoneById() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    when(mapper.getById(1l)).thenReturn(zone);

    DeliveryZone returnedZone = repository.getById(1l);

    verify(mapper).getById(1l);
    assertThat(returnedZone, is(zone));
  }
}
