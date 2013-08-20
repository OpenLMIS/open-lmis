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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor
public class ConfigurationSettingService {

  private ConfigurationRepository configurationRepository;


  @Autowired
  public ConfigurationSettingService(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  public ConfigurationSetting getByKey(String key){
      return configurationRepository.getByKey(key);
  }

  public int getConfigurationIntValue(String key){

    ConfigurationSetting configuration = getByKey(key);

      if(configuration == null || configuration.getValue() == null || configuration.getValue().isEmpty())
          return 0;

      return Integer.parseInt(configuration.getValue());
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
    return configurationRepository.getAll();
  }

  public void update(List<ConfigurationSetting> settings){
    for(ConfigurationSetting conf : settings){
         configurationRepository.setValue(conf);
    }
  }


}
