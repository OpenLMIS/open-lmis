/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.builder.DeliveryZoneProgramScheduleBuilder;
import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.openlmis.allocation.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.domain.BaseModel;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
