/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.DeliveryZoneBuilder;
import org.openlmis.core.builder.DeliveryZoneProgramScheduleBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneProgramSchedule;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.code;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
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
  @Autowired
  QueryExecutor queryExecutor;

  private ProcessingSchedule processingSchedule;
  private DeliveryZone deliveryZone;
  private Program program;
  private DeliveryZoneProgramSchedule deliveryZoneProgramSchedule;

  @Before
  public void setUp() throws Exception {
    processingSchedule = make(a(ProcessingScheduleBuilder.defaultProcessingSchedule));

    deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    program = make(a(ProgramBuilder.defaultProgram));

    programMapper.insert(program);
    scheduleMapper.insert(processingSchedule);
    deliveryZoneMapper.insert(deliveryZone);

    deliveryZoneProgramSchedule = make(a(DeliveryZoneProgramScheduleBuilder.defaultDZProgramSchedule,
      with(DeliveryZoneProgramScheduleBuilder.program, program),
      with(DeliveryZoneProgramScheduleBuilder.schedule, processingSchedule), with(DeliveryZoneProgramScheduleBuilder.zone, deliveryZone)));
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
    assertThat(actual.getCreatedDate(), CoreMatchers.is(CoreMatchers.notNullValue()));
    actual.setCreatedDate(null);

    assertThat(actual.getProgram().getId(), CoreMatchers.is(expectedDZPS.getProgram().getId()));
    assertThat(actual.getDeliveryZone().getId(), CoreMatchers.is(expectedDZPS.getDeliveryZone().getId()));
    assertThat(actual.getSchedule().getId(), CoreMatchers.is(expectedDZPS.getSchedule().getId()));
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

  @Test
  public void shouldGetProgramIdsForDeliveryZone() throws Exception {
    mapper.insert(deliveryZoneProgramSchedule);
    List<Long> programIds = mapper.getProgramsIdsForDeliveryZones(deliveryZoneProgramSchedule.getDeliveryZone().getId());

    assertThat(programIds.get(0), CoreMatchers.is(deliveryZoneProgramSchedule.getProgram().getId()));
    assertThat(programIds.size(), CoreMatchers.is(1));
  }

  @Test
  public void shouldGetProcessingScheduleForProgramInDeliveryZone() throws Exception {
    long deliveryZoneId = insertDeliveryZone("deliveryZoneCode", "DZ name");
    Long scheduleId = insertSchedule();
    insertDeliveryZoneProgramSchedule(deliveryZoneId, scheduleId);

    ProcessingSchedule processingSchedule = mapper.getProcessingScheduleByZoneAndProgram(deliveryZoneId, getProgramId());

    assertThat(processingSchedule.getId(), CoreMatchers.is(scheduleId));
  }

  private Long getProgramId() throws SQLException {
    try (ResultSet resultSet = queryExecutor.execute("SELECT id FROM programs WHERE code = 'VACCINES'")) {
      resultSet.next();
      return resultSet.getLong(1);
    }
  }

  private void insertDeliveryZoneProgramSchedule(long id, Long scheduleId) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO delivery_zone_program_schedules(deliveryZoneId, programId, scheduleId ) " +
      "VALUES(?,(SELECT id FROM programs WHERE code='VACCINES'), ?)", id, scheduleId);
  }

  private long insertSchedule() throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO processing_schedules(code, name, description) values(?, ?, ?)",
      "M", "scheduleName", "desc");
  }

  private long insertDeliveryZone(String deliveryZoneCode, String deliveryZoneName) throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO delivery_zones (code, name) values (?,?)", deliveryZoneCode, deliveryZoneName);
  }
}
