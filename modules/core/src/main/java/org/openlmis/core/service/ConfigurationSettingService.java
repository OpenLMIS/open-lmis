/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
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
