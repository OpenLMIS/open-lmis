/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.PriceSchedule;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class PriceScheduleMapperIT {

  @Autowired
  private PriceScheduleMapper mapper;

  @Autowired
  private QueryExecutor executor;

  @Test
  public void shouldGetById() throws Exception {
    executor.executeQuery("insert into price_schedules (code, description) values ('A', 'Description')");
    ResultSet r = executor.execute("select * from price_schedules");
    r.next();
    PriceSchedule schedule = mapper.getById(r.getLong("id"));
    assertThat(schedule, is(notNullValue()));
    assertThat(schedule.getCode(), is("A"));
  }

  @Test
  public void shouldGetAll() throws Exception {
    executor.executeQuery("insert into price_schedules (code, description) values ('A', 'Description')");
    List<PriceSchedule> schedules = mapper.getAll();
    assertThat(schedules.size(), is(1));
  }

  @Test
  public void shouldGetByCode() throws Exception {
    executor.executeQuery("insert into price_schedules (code, description) values ('A', 'Description')");
    PriceSchedule schedule = mapper.getByCode("A");
    assertThat(schedule, is(notNullValue()));
    assertThat(schedule.getDescription(), is("Description"));
  }
}