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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.ProcessingPeriodBuilder;
import org.openlmis.core.builder.ProcessingScheduleBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.code;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.defaultProcessingPeriod;
import static org.openlmis.core.builder.ProcessingPeriodBuilder.scheduleId;
import static org.openlmis.core.builder.ProcessingScheduleBuilder.defaultProcessingSchedule;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class BudgetLineItemMapperIT {

  @Autowired
  private BudgetFileMapper budgetFileMapper;

  @Autowired
  private BudgetLineItemMapper mapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Autowired
  private ProcessingScheduleMapper scheduleMapper;

  @Autowired
  private ProcessingPeriodMapper processingPeriodMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  private BudgetLineItem budgetLineItem;

  private BudgetFileInfo budgetFileInfo;

  private ProcessingPeriod period;

  private Facility facility;

  @Before
  public void setUp() throws Exception {

    ProcessingSchedule schedule = make(a(defaultProcessingSchedule, with(ProcessingScheduleBuilder.code, "Q1")));
    scheduleMapper.insert(schedule);

    period = make(a(defaultProcessingPeriod,
      with(ProcessingPeriodBuilder.startDate, new Date()),
      with(scheduleId, schedule.getId())));
    processingPeriodMapper.insert(period);

    budgetFileInfo = new BudgetFileInfo("Budget File", false);
    budgetFileMapper.insert(budgetFileInfo);

    String facilityCode = "F1011";
    facility = make(a(defaultFacility, with(code, facilityCode)));
    facilityMapper.insert(facility);

    budgetLineItem = new BudgetLineItem(facility.getId(), 1L, period.getId(), budgetFileInfo.getId(), new Date(), BigDecimal.valueOf(345.45), "My good notes");
  }

  @Test
  public void shouldInsertBudgetLineItem() throws Exception {
    mapper.insert(budgetLineItem);

    assertThat(budgetLineItem.getId(), is(notNullValue()));
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM budget_line_items WHERE budgetFileId = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getLong("id"), is(budgetLineItem.getId()));
  }

  @Test
  public void shouldUpdateBudgetLineItem() throws Exception {
    mapper.insert(budgetLineItem);

    BudgetLineItem duplicatedBudgetLineItem = new BudgetLineItem(facility.getId(), 1L, period.getId(), budgetFileInfo.getId(), new Date(), BigDecimal.valueOf(345.45), "My updated good notes");

    duplicatedBudgetLineItem.setId(budgetLineItem.getId());

    mapper.update(duplicatedBudgetLineItem);
    ResultSet resultSet = queryExecutor.execute("SELECT * FROM budget_line_items WHERE budgetFileId = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getString("notes"), is("My updated good notes"));
  }

  @Test
  public void shouldGetBudgetLineItemByFacilityIdProgramIdAndPeriodId() {
    mapper.insert(budgetLineItem);

    BudgetLineItem savedLineItem = mapper.get(facility.getId(), 1L, period.getId());

    assertThat(savedLineItem.getId(), is(budgetLineItem.getId()));
  }
}
