/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.allocation.builder.DeliveryZoneBuilder;
import org.openlmis.allocation.builder.DeliveryZoneProgramScheduleBuilder;
import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.allocation.builder.DeliveryZoneProgramScheduleBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.code;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-allocation.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneProgramScheduleMapperIT {

  @Autowired
  DeliveryZoneProgramScheduleMapper mapper;
  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;
  @Autowired
  ProcessingScheduleMapper scheduleMapper;
  @Autowired
  ProgramMapper programMapper;

  private ProcessingSchedule processingSchedule;
  private DeliveryZone deliveryZone;
  private Program program;
  private DeliveryZoneProgramSchedule deliveryZoneProgramSchedule;

  @Before
  public void setUp() throws Exception {
    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));

    deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    program = make(a(defaultProgram));

    programMapper.insert(program);
    scheduleMapper.insert(processingSchedule);
    deliveryZoneMapper.insert(deliveryZone);

    deliveryZoneProgramSchedule = make(a(defaultDZProgramSchedule,
        with(DeliveryZoneProgramScheduleBuilder.program, program),
        with(schedule, processingSchedule), with(zone, deliveryZone)));
  }

  @Test
  public void shouldInsertDZProgramSchedule() throws Exception {
    mapper.insert(deliveryZoneProgramSchedule);

    DeliveryZoneProgramSchedule returnedValue = mapper.getByDeliveryZoneCodeAndProgramCode(deliveryZone.getCode(), program.getCode());

    assertDZPSEquals(deliveryZoneProgramSchedule, returnedValue);
  }

  private void assertDZPSEquals(DeliveryZoneProgramSchedule deliveryZoneProgramSchedule, DeliveryZoneProgramSchedule actual) {
    DeliveryZoneProgramSchedule expectedDZPS = new DeliveryZoneProgramSchedule(deliveryZone.getId(), program.getId(), processingSchedule.getId());
    expectedDZPS.setCreatedBy(deliveryZoneProgramSchedule.getCreatedBy());
    assertThat(actual.getCreatedDate(), is(notNullValue()));
    actual.setCreatedDate(null);
    assertThat(actual, is(expectedDZPS));
  }

  @Test
  public void shouldUpdateDZProgramSchedule() throws Exception {
    mapper.insert(deliveryZoneProgramSchedule);
    ProcessingSchedule processingSchedule1 = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule, with(code, "HalfYearly")));
    scheduleMapper.insert(processingSchedule1);
    deliveryZoneProgramSchedule.setSchedule(processingSchedule);

    mapper.update(deliveryZoneProgramSchedule);

    DeliveryZoneProgramSchedule returned = mapper.getByDeliveryZoneCodeAndProgramCode(deliveryZone.getCode(), program.getCode());

    assertDZPSEquals(deliveryZoneProgramSchedule, returned);
  }
}
