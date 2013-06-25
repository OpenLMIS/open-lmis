/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.repository.DeliveryZoneRepository;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.MANAGE_DISTRIBUTION;

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
  public void shouldGetProgramForDeliveryZoneBasedOnUserRights() throws Exception {
    service.getProgramsForDeliveryZone(1l);

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

    List<Program> returnedPrograms = service.getProgramsForDeliveryZone(1l);

    assertThat(returnedPrograms, hasItem(activeProgram));
    assertThat(returnedPrograms.size(), is(1));
  }
}
