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
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.repository.DeliveryZoneProgramScheduleRepository;
import org.openlmis.core.repository.mapper.DeliveryZoneProgramScheduleMapper;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
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

  @Test
  public void shouldGetProcessingScheduleByZoneAndProgram() throws Exception {
    ProcessingSchedule expectedSchedule = new ProcessingSchedule();
    expectedSchedule.setId(3l);
    when(mapper.getProcessingScheduleByZoneAndProgram(1l, 2l)).thenReturn(expectedSchedule);

    ProcessingSchedule processingSchedule = repository.getProcessingScheduleByZoneAndProgram(1l, 2l);

    assertThat(processingSchedule, is(expectedSchedule));
    verify(mapper).getProcessingScheduleByZoneAndProgram(1l, 2l);
  }
}
