/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.repository.mapper;


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
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.repository.mapper.DistributionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openlmis.builder.DistributionBuilder.*;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.domain.DistributionStatus.INITIATED;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-distribution.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class DistributionMapperTest {

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
  Program program1;
  ProcessingPeriod processingPeriod;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();


  @Before
  public void setUp() throws Exception {
    zone = make(a(defaultDeliveryZone));
    program1 = make(a(defaultProgram));

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    deliveryZoneMapper.insert(zone);
    programMapper.insert(program1);
    periodMapper.insert(processingPeriod);

  }

  @Test
  public void shouldInsertDistributionInInitiatedState() throws Exception {


    Distribution distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    mapper.insert(distribution);

    List params = Arrays.asList(distribution.getId());
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM distributions WHERE id = ?", params);
    resultSet.next();


    assertNotNull(distribution.getId());
    assertThat(resultSet.getLong("deliveryZoneId"), is(zone.getId()));
    assertThat(resultSet.getLong("programId"), is(program1.getId()));
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
      with(program, program1)));

    mapper.insert(distribution);

    Distribution duplicateDistribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    expectedException.expect(DuplicateKeyException.class);
    expectedException.expectMessage("duplicate key value violates unique constraint");

    mapper.insert(duplicateDistribution);
  }

  @Test
  public void shouldGetExistingDistributionIfExists() throws Exception {
    Distribution distribution = make(a(defaultDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    mapper.insert(distribution);

    Distribution distributionFromDatabase = mapper.get(distribution);

    assertThat(distributionFromDatabase.getProgram().getId(), is(distribution.getProgram().getId()));
    assertThat(distributionFromDatabase.getPeriod().getId(), is(distribution.getPeriod().getId()));
    assertThat(distributionFromDatabase.getDeliveryZone().getId(), is(distribution.getDeliveryZone().getId()));
  }
}
