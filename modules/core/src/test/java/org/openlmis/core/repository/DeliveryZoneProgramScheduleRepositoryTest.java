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
