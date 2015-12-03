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
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.DeliveryZoneRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneServiceTest {

  @Mock
  DeliveryZoneRepository repository;

  @Mock
  ProgramService programService;

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
    when(repository.getByUserForRight(1l, MANAGE_DISTRIBUTION)).thenReturn(deliveryZones);

    List<DeliveryZone> returnedZones = service.getByUserForRight(1l, MANAGE_DISTRIBUTION);

    verify(repository).getByUserForRight(1l, MANAGE_DISTRIBUTION);
    assertThat(returnedZones, is(deliveryZones));
  }

  @Test
  public void shouldGetAllDeliveryZones() throws Exception {
    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(repository.getAll()).thenReturn(deliveryZones);

    List<DeliveryZone> returnedZones = service.getAll();

    verify(repository).getAll();
    assertThat(returnedZones, is(deliveryZones));
  }

  @Test
  public void shouldGetProgramForDeliveryZoneBasedOnUserRights() throws Exception {
    service.getActiveProgramsForDeliveryZone(1l);

    verify(repository).getPrograms(1l);
  }

  @Test
  public void shouldGetOnlyActiveProgramsForDeliveryZone() throws Exception {
    final Program activeProgram = new Program(1l);
    activeProgram.setActive(true);

    final Program inActiveProgram = new Program(2l);
    inActiveProgram.setActive(false);

    List<Program> programs = new ArrayList<Program>() {{
      add(activeProgram);
      add(inActiveProgram);
    }};

    when(repository.getPrograms(1l)).thenReturn(programs);
    when(programService.getById(1l)).thenReturn(activeProgram);
    when(programService.getById(2l)).thenReturn(inActiveProgram);

    List<Program> returnedPrograms = service.getActiveProgramsForDeliveryZone(1l);

    assertThat(returnedPrograms, hasItem(activeProgram));
    assertThat(returnedPrograms.size(), is(1));
  }

  @Test
  public void shouldGetOnlyAllFilledProgramsForDeliveryZone() throws Exception {
    final Program program1 = new Program(1l);

    final Program program2 = new Program(2l);

    List<Program> programs = new ArrayList<Program>() {{
      add(program1);
      add(program2);
    }};

    when(repository.getPrograms(1l)).thenReturn(programs);
    when(programService.getById(1l)).thenReturn(program1);
    when(programService.getById(2l)).thenReturn(program2);

    List<Program> returnedPrograms = service.getAllProgramsForDeliveryZone(1l);

    assertThat(returnedPrograms, hasItem(program1));
    assertThat(returnedPrograms, hasItem(program2));
    assertThat(returnedPrograms.size(), is(2));
  }

  @Test
  public void shouldGetDeliveryZoneById() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    when(repository.getById(1l)).thenReturn(zone);

    DeliveryZone returnedZone = service.getById(1l);

    verify(repository).getById(1l);
    assertThat(returnedZone, is(zone));
  }
}
