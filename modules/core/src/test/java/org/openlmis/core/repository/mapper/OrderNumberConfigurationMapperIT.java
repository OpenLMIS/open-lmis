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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.OrderNumberConfiguration;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class OrderNumberConfigurationMapperIT {

  @Autowired
  OrderNumberConfigurationMapper orderNumberConfigurationMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldGetDefaultOrderConfiguration() {
    OrderNumberConfiguration orderNumberConfiguration = orderNumberConfigurationMapper.get();
    assertThat(orderNumberConfiguration.getOrderNumberPrefix(), is("O"));
    assertThat(orderNumberConfiguration.getIncludeOrderNumberPrefix(), is(true));
    assertThat(orderNumberConfiguration.getIncludeProgramCode(), is(true));
    assertThat(orderNumberConfiguration.getIncludeSequenceCode(), is(true));
    assertThat(orderNumberConfiguration.getIncludeRnrTypeSuffix(), is(true));
  }

  @Test
  public void shouldInsertOrderConfiguration() throws SQLException {
    queryExecutor.executeQuery("DELETE FROM order_number_configuration");

    OrderNumberConfiguration orderNumberConfiguration = new OrderNumberConfiguration("Order", true, false, true, false);
    orderNumberConfigurationMapper.insert(orderNumberConfiguration);
    OrderNumberConfiguration savedOrderConfiguration = orderNumberConfigurationMapper.get();
    assertThat(savedOrderConfiguration.getOrderNumberPrefix(), is("Order"));
    assertThat(savedOrderConfiguration.getIncludeOrderNumberPrefix(), is(true));
    assertThat(savedOrderConfiguration.getIncludeProgramCode(), is(false));
    assertThat(savedOrderConfiguration.getIncludeSequenceCode(), is(true));
    assertThat(savedOrderConfiguration.getIncludeRnrTypeSuffix(), is(false));
  }

  @Test
  public void shouldDeleteOrderConfiguration() {
    orderNumberConfigurationMapper.delete();
    assertThat(orderNumberConfigurationMapper.get(), is(nullValue()));
  }
}
