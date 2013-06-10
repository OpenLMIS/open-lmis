/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.openlmis.allocation.repository.mapper.DeliveryZoneProgramScheduleMapper;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneProgramScheduleRepositoryTest {

  @InjectMocks
  DeliveryZoneProgramScheduleRepository repository;

  @Mock
  DeliveryZoneProgramScheduleMapper mapper;

  @Test
  public void shouldInsertDZProgramSchedule() throws Exception {
    DeliveryZoneProgramSchedule schedule = new DeliveryZoneProgramSchedule();
    repository.insert(schedule);
    verify(mapper).insert(schedule);
  }

  @Test
  public void shouldUpdateDZProgramSchedule() throws Exception {
    DeliveryZoneProgramSchedule schedule = new DeliveryZoneProgramSchedule();
    repository.update(schedule);
    verify(mapper).update(schedule);
  }
}
