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

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.repository.ConfigurationSettingRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationSettingServiceTest {

  private ConfigurationSetting expected;

  @Mock
  private ConfigurationSettingRepository repository;

  @InjectMocks
  private ConfigurationSettingService service;

  @Before
  public void setup() throws Exception {
    expected = new ConfigurationSetting();
    expected.setKey("KEY_NAME");
    when(repository.getByKey("KEY_NAME")).thenReturn(expected);

  }

  @Test
  public void shouldGetByKey() throws Exception {

    ConfigurationSetting actual = service.getByKey("KEY_NAME");

    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual.getKey(), expected.getKey());
  }

  @Test
  public void shouldGetConfigurationIntValueDefaultToZero() throws Exception {
    expected.setValue("1");
    when(repository.getByKey("UN_EXISTING_KEY")).thenReturn(null);
    int actual = service.getConfigurationIntValue("UN_EXISTING_KEY");

    verify(repository).getByKey("UN_EXISTING_KEY");
    assertEquals(actual, 0);

    expected.setValue(null);
    actual = service.getConfigurationIntValue("KEY_NAME");
    assertEquals(actual, 0);

    expected.setValue("");
    actual = service.getConfigurationIntValue("KEY_NAME");
    assertEquals(actual, 0);
  }

  @Test(expected = NumberFormatException.class)
  public void shouldRaiseNumberFormatExceptionWhenValueIsNotNumber() throws Exception {
    expected.setValue("SOME_RANDOM_CHARACTER");
    int actual = service.getConfigurationIntValue("KEY_NAME");
  }

  @Test
  public void shouldGetConfigurationIntValue() throws Exception {
    expected.setValue("1");

    int actual = service.getConfigurationIntValue("KEY_NAME");

    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual, 1);
  }

  @Test
  public void shouldGetConfigurationStringValue() throws Exception {
    expected.setValue("Tanzania");

    String actual = service.getConfigurationStringValue("KEY_NAME");

    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual, "Tanzania");
  }

  @Test
  public void shouldGetConfigurationStringValueDefaultEmptyString() throws Exception {
    expected.setValue(null);

    String actual = service.getConfigurationStringValue("KEY_NAME");
    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual, "");

    expected.setValue("");

    actual = service.getConfigurationStringValue("KEY_NAME");
    verify(repository, atLeastOnce()).getByKey("KEY_NAME");
    assertEquals(actual, "");

    when(repository.getByKey("UN_EXISTING_KEY")).thenReturn(null);
    actual = service.getConfigurationStringValue("UN_EXISTING_KEY");
    verify(repository, atLeastOnce()).getByKey("UN_EXISTING_KEY");

  }

  @Test
  public void shouldGetBoolValue() throws Exception {
    expected.setValue("true");

    boolean actual = service.getBoolValue("KEY_NAME");

    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual, true);
  }

  @Test
  public void shouldGetBoolValueDefaultFalse() throws Exception {
    expected.setValue("ErrorValue");

    boolean actual = service.getBoolValue("KEY_NAME");

    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual, false);
  }

  @Test
  public void shouldGetConfigurationListValue() throws Exception {
    expected.setValue("1,2,3");

    List<Object> actual = service.getConfigurationListValue("KEY_NAME", ",");

    verify(repository).getByKey("KEY_NAME");
    assertEquals(actual.size(), 3);
  }

  @Test
  public void shouldGetConfigurations() throws Exception {
    when(repository.getAll()).thenReturn(new ArrayList<ConfigurationSetting>());

    List<ConfigurationSetting> actual = service.getConfigurations();

    verify(repository).getAll();
    assertEquals(actual.size(), 0);
  }

  @Test
  public void shouldUpdateAllSettings() throws Exception {
    List<ConfigurationSetting> settings = new ArrayList<ConfigurationSetting>();
    settings.add(new ConfigurationSetting());
    settings.add(new ConfigurationSetting());

    service.update(settings);

    verify(repository, atLeastOnce()).setValue(Matchers.any(ConfigurationSetting.class));
  }
}
