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


import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class ConfigurationSettingService {

  private ConfigurationSettingRepository configurationSettingRepository;


  @Autowired
  public ConfigurationSettingService(ConfigurationSettingRepository configurationSettingRepository) {
    this.configurationSettingRepository = configurationSettingRepository;
  }

  public ConfigurationSetting getByKey(String key) {
    return configurationSettingRepository.getByKey(key);
  }

  public int getConfigurationIntValue(String key) {
    ConfigurationSetting configuration = getByKey(key);
    if (configuration == null || configuration.getValue() == null || configuration.getValue().isEmpty())
      return 0;
    return Integer.parseInt(configuration.getValue());
  }


  public String getConfigurationStringValue(String key) {
    ConfigurationSetting configurationSetting = getByKey(key);
    if (configurationSetting == null || configurationSetting.getValue() == null || configurationSetting.getValue().isEmpty())
      return "";
    return configurationSetting.getValue();
  }

  public Boolean getBoolValue(String key) {
    String value = getConfigurationStringValue(key);

    // if the configuration was not set at all in the configurations table ... return false
    if (value.isEmpty())
      return false;

    return Boolean.parseBoolean(value);
  }

  public List<Object> getConfigurationListValue(String key, String delimiter) {
    ConfigurationSetting configuration = getByKey(key);

    if (configuration == null || configuration.getValue() == null || configuration.getValue().isEmpty())
      return null;
    List<Object> values = new ArrayList<>();
    if (configuration.getValue().contains(delimiter)) {
      for (String value : configuration.getValue().split(delimiter)) {
        values.add(value);
      }
    }
    return values;
  }

  public List<ConfigurationSetting> getConfigurations() {
    return configurationSettingRepository.getAll();
  }

  public void update(List<ConfigurationSetting> settings) {
    for (ConfigurationSetting conf : settings) {
      configurationSettingRepository.setValue(conf);
    }
  }

  public void saveBooleanValue(String key, Boolean value) {
    ConfigurationSetting setting = configurationSettingRepository.getByKey(key);
    setting.setValue(value.toString());
    configurationSettingRepository.setValue(setting);
  }

  public List<ConfigurationSetting> getSearchResults(String s) {
    return configurationSettingRepository.getSearchResults(s);
  }
}
