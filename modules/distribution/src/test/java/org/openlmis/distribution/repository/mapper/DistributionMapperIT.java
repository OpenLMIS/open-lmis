/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.distribution.repository.mapper;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.domain.Program;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.DeliveryZoneMapper;
import org.openlmis.core.repository.mapper.ProcessingPeriodMapper;
import org.openlmis.core.repository.mapper.ProcessingScheduleMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.builder.DistributionBuilder;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.DistributionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.*;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.builder.DistributionBuilder.*;
import static org.openlmis.distribution.domain.DistributionStatus.INITIATED;
import static org.openlmis.distribution.domain.DistributionStatus.SYNCED;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class DistributionMapperIT {

  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingPeriodMapper periodMapper;

  @Autowired
  DistributionMapper mapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  DeliveryZone zone;
  Program program;
  ProcessingPeriod processingPeriod;
  ProcessingSchedule schedule;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();


  @Before
  public void setUp() throws Exception {
    zone = make(a(defaultDeliveryZone));
    program = make(a(defaultProgram));

    schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    deliveryZoneMapper.insert(zone);
    programMapper.insert(program);
    periodMapper.insert(processingPeriod);
  }

  @Test
  public void shouldInsertDistributionInInitiatedState() throws Exception {
    Distribution distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program)));

    mapper.insert(distribution);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM distributions WHERE id = ?", distribution.getId());
    resultSet.next();


    assertNotNull(distribution.getId());
    assertThat(resultSet.getLong("deliveryZoneId"), is(zone.getId()));
    assertThat(resultSet.getLong("programId"), is(program.getId()));
    assertThat(resultSet.getLong("periodId"), is(processingPeriod.getId()));
    assertThat(resultSet.getString("status"), is(INITIATED.toString()));
    assertThat(resultSet.getLong("createdBy"), is(distribution.getCreatedBy()));
    assertThat(resultSet.getLong("modifiedBy"), is(distribution.getModifiedBy()));
    assertThat(resultSet.getDate("createdDate"), is(notNullValue()));
    assertThat(resultSet.getDate("modifiedDate"), is(notNullValue()));
  }

  @Test
  public void shouldThrowErrorForDuplicateDZPPCombination() {
    Distribution distribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program)));

    mapper.insert(distribution);

    Distribution duplicateDistribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program)));

    expectedException.expect(DuplicateKeyException.class);
    expectedException.expectMessage("duplicate key value violates unique constraint");

    mapper.insert(duplicateDistribution);
  }

  @Test
  public void shouldGetExistingDistributionIfExists() throws Exception {
    Distribution distribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program)));

    mapper.insert(distribution);

    Distribution distributionFromDatabase = mapper.get(distribution);

    assertThat(distributionFromDatabase.getProgram().getId(), is(distribution.getProgram().getId()));
    assertThat(distributionFromDatabase.getPeriod().getId(), is(distribution.getPeriod().getId()));
    assertThat(distributionFromDatabase.getDeliveryZone().getId(), is(distribution.getDeliveryZone().getId()));
  }

  @Test
  public void shouldGetAllPeriodsForAZoneAndProgramForWhichDistributionIsSynced() {
    Distribution synedDistribution1 = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program),
      with(status, DistributionStatus.SYNCED)));
    mapper.insert(synedDistribution1);

    ProcessingPeriod processingPeriod2 = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId()), with(name, "Month2")));
    periodMapper.insert(processingPeriod2);
    Distribution synedDistribution2 = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod2),
      with(DistributionBuilder.program, program),
      with(status, DistributionStatus.SYNCED)));
    mapper.insert(synedDistribution2);

    ProcessingPeriod processingPeriod3 = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId()), with(name, "Month3")));
    periodMapper.insert(processingPeriod3);
    Distribution unsynedDistribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod3),
      with(DistributionBuilder.program, program)));
    mapper.insert(unsynedDistribution);

    List<Long> syncedPeriodsForDeliveryZoneAndProgram = mapper.getSyncedPeriodsForDeliveryZoneAndProgram(zone.getId(), program.getId());

    assertThat(syncedPeriodsForDeliveryZoneAndProgram.size(), is(2));
    assertTrue(syncedPeriodsForDeliveryZoneAndProgram.contains(processingPeriod.getId()));
    assertTrue(syncedPeriodsForDeliveryZoneAndProgram.contains(processingPeriod2.getId()));
  }

  @Test
  public void shouldUpdateDistributionStatus() {
    Distribution distribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program)));

    mapper.insert(distribution);
    mapper.updateDistributionStatus(distribution.getId(), SYNCED, 1L);

    Distribution distributionFromDatabase = mapper.get(distribution);

    assertThat(distributionFromDatabase.getStatus(), is(SYNCED));
  }

  @Test
  public void shouldReturnDistributionById() {
    Distribution distribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(DistributionBuilder.program, program)));

    mapper.insert(distribution);

    Distribution distributionFromDatabase = mapper.getBy(distribution.getId());

    assertThat(distributionFromDatabase.getProgram().getId(), is(distribution.getProgram().getId()));
    assertThat(distributionFromDatabase.getPeriod().getId(), is(distribution.getPeriod().getId()));
    assertThat(distributionFromDatabase.getDeliveryZone().getId(), is(distribution.getDeliveryZone().getId()));
  }
}
