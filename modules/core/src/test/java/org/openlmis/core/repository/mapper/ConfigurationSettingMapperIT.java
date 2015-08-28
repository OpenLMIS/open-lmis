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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@Category(IntegrationTests.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ConfigurationSettingMapperIT {

  public static final String COUNTRY_KEY = "COUNTRY";

  @Autowired
  private ConfigurationSettingMapper mapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void shouldGetByKey() throws Exception {
    ConfigurationSetting setting = mapper.getByKey(COUNTRY_KEY);
    assertEquals(setting.getKey(), COUNTRY_KEY);
  }

  @Test
  public void shouldGetAll() throws Exception {
    ResultSet resultSet = queryExecutor.execute("select count(*) count from configuration_settings where isConfigurable");
    resultSet.next();
    int expectedCount = resultSet.getInt("count");

    List<ConfigurationSetting> settings = mapper.getAll();

    assertEquals(settings.size(), expectedCount);
  }

  @Test
  public void shouldGetAllVerifyFirstAndLastElements() throws Exception {
    ResultSet resultSet = queryExecutor.execute("select * from configuration_settings order by groupName, displayOrder, name");
    resultSet.next();
    String expectedFirstKey = resultSet.getString("key");

    List<ConfigurationSetting> settings = mapper.getAll();

    assertEquals(settings.get(0).getKey(), expectedFirstKey);
  }

  @Test
  public void shouldUpdateValue() throws Exception {
    queryExecutor.executeUpdate("update configuration_settings set value = '123' where key = 'COUNTRY' ");

    ConfigurationSetting setting = mapper.getByKey(COUNTRY_KEY);
    setting.setValue("234");
    mapper.updateValue(setting);

    ConfigurationSetting updatedSetting = mapper.getByKey(COUNTRY_KEY);
    assertEquals("234", updatedSetting.getValue());
  }
}
