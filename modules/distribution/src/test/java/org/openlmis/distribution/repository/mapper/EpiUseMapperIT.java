/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.core.repository.mapper.*;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.distribution.builder.DistributionBuilder.*;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-distribution.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EpiUseMapperIT {

  @Autowired
  DeliveryZoneMapper deliveryZoneMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  ProcessingPeriodMapper periodMapper;

  @Autowired
  DistributionMapper distributionMapper;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  EpiUseMapper mapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private ProductGroupMapper productGroupMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  DeliveryZone zone;
  Program program1;
  ProcessingPeriod processingPeriod;
  Distribution distribution;
  Facility facility;

  @Before
  public void setUp() throws Exception {
    zone = make(a(defaultDeliveryZone));
    program1 = make(a(defaultProgram));
    facility = make(a(defaultFacility));
    ProcessingSchedule schedule = make(a(defaultProcessingSchedule));
    scheduleMapper.insert(schedule);

    processingPeriod = make(a(defaultProcessingPeriod, with(scheduleId, schedule.getId())));

    deliveryZoneMapper.insert(zone);
    programMapper.insert(program1);
    periodMapper.insert(processingPeriod);


    distribution = make(a(initiatedDistribution,
      with(deliveryZone, zone),
      with(period, processingPeriod),
      with(program, program1)));

    distributionMapper.insert(distribution);

    facilityMapper.insert(facility);

  }

  @Test
  public void shouldSaveEpiUse() throws Exception {
    EpiUse epiUse = new EpiUse(distribution.getId(), facility.getId(), new ArrayList<EpiUseLineItem>());
    mapper.insert(epiUse);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM epi_use WHERE id = " + epiUse.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("id"), is(epiUse.getId()));
  }

  @Test
  public void shouldSaveEpiUseLineItem() throws Exception {
    ProductGroup productGroup = new ProductGroup("PG1", "Product Group 1");
    productGroupMapper.insert(productGroup);

    EpiUse epiUse = new EpiUse(distribution.getId(), facility.getId(), new ArrayList<EpiUseLineItem>());
    mapper.insert(epiUse);

    EpiUseLineItem epiUseLineItem = new EpiUseLineItem();
    epiUseLineItem.setProductGroup(productGroup);
    mapper.insertLineItem(epiUseLineItem);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM epi_use_line_items WHERE id = " + epiUseLineItem.getId());
    resultSet.next();
    assertThat(resultSet.getLong("productGroupId"), is(epiUseLineItem.getProductGroup().getId()));
  }
}
