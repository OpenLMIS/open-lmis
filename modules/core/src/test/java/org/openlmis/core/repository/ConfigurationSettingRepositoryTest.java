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

package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.repository.mapper.ConfigurationSettingMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationSettingRepositoryTest {

  @Mock
  private ConfigurationSettingMapper mapper;

  @InjectMocks
  private ConfigurationSettingRepository repository;


  @Test
  public void shouldGetByKey() throws Exception {

    ConfigurationSetting expectedConfiguration = new ConfigurationSetting();
    expectedConfiguration.setKey("COUNTRY");

    when(mapper.getByKey("123")).thenReturn(expectedConfiguration);

    ConfigurationSetting actual = repository.getByKey("123");

    verify(mapper).getByKey("123");
    assertEquals(actual.getKey(), expectedConfiguration.getKey());

  }


  @Test
  public void shouldGetAll() throws Exception {
    List<ConfigurationSetting> expectedConfigSettings = new ArrayList<ConfigurationSetting>();
    expectedConfigSettings.add(new ConfigurationSetting());
    when(mapper.getAll()).thenReturn(expectedConfigSettings);

    List<ConfigurationSetting> actualSettings = repository.getAll();
    verify(mapper).getAll();
    assertEquals(expectedConfigSettings.size(), actualSettings.size());
  }


  @Test
  public void shouldSetValue() throws Exception {
    ConfigurationSetting setting = new ConfigurationSetting();
    setting.setKey("COUNTRY");

    repository.setValue(setting);

    verify(mapper).updateValue(setting);
  }


} 
