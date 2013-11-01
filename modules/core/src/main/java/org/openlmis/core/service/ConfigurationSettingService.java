/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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

  public ConfigurationSetting getByKey(String key){
      return configurationSettingRepository.getByKey(key);
  }

  public int getConfigurationIntValue(String key){

    ConfigurationSetting configuration = getByKey(key);

      if(configuration == null || configuration.getValue() == null || configuration.getValue().isEmpty())
          return 0;

      return Integer.parseInt(configuration.getValue());
  }

  public String getConfigurationStringValue(String key){
      ConfigurationSetting configurationSetting = getByKey(key);
      if (configurationSetting == null || configurationSetting.getValue() == null || configurationSetting.getValue().isEmpty())
          return "";
      return configurationSetting.getValue();
  }

  public List<Object> getConfigurationListValue(String key, String delimiter){
    ConfigurationSetting configuration = getByKey(key);

      if(configuration == null || configuration.getValue() == null || configuration.getValue().isEmpty())
        return null;
      List<Object> values = new ArrayList<>();
      if(configuration.getValue().contains(delimiter)){
          for(String value : configuration.getValue().split(delimiter)){
              values.add(value);
          }
      }
      return values;
  }

  public List<ConfigurationSetting> getConfigurations(){
    return configurationSettingRepository.getAll();
  }

  public void update(List<ConfigurationSetting> settings){
    for(ConfigurationSetting conf : settings){
         configurationSettingRepository.setValue(conf);
    }
  }


}
