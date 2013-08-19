/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
