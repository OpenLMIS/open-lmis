/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.upload.DeliveryZoneProgramScheduleHandler;
import org.openlmis.core.builder.DeliveryZoneProgramScheduleBuilder;
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.db.categories.UnitTests;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneProgramScheduleHandlerTest {

  @Mock
  DeliveryZoneProgramScheduleService service;

  @InjectMocks
  DeliveryZoneProgramScheduleHandler handler;


  @Test
  public void shouldSaveDZProgramSchedule() throws Exception {
    DeliveryZoneProgramSchedule schedule = new DeliveryZoneProgramSchedule();

    handler.save(schedule);

    verify(service).save(schedule);
  }

  @Test
  public void shouldGetDZProgramScheduleByDeliveryZoneCodeAndProgramCode() throws Exception {
    DeliveryZoneProgramSchedule schedule = make(a(DeliveryZoneProgramScheduleBuilder.defaultDZProgramSchedule));
    DeliveryZoneProgramSchedule expectedDZPS = new DeliveryZoneProgramSchedule();
    when(service.getByDeliveryZoneCodeAndProgramCode(schedule.getDeliveryZone().getCode(), schedule.getProgram().getCode()))
        .thenReturn(expectedDZPS);

    BaseModel returned = handler.getExisting(schedule);

    verify(service).getByDeliveryZoneCodeAndProgramCode(schedule.getDeliveryZone().getCode(), schedule.getProgram().getCode());
    assertThat(expectedDZPS, is((DeliveryZoneProgramSchedule)returned));
  }
}
