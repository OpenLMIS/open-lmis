/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Configuration;
import org.openlmis.core.repository.mapper.ConfigurationSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ConfigurationRepository {

  private ConfigurationSettingMapper mapper;

  @Autowired
  public ConfigurationRepository(ConfigurationSettingMapper configurationSettingMapper) {
    this.mapper = configurationSettingMapper;
  }

  public Configuration getByKey(String  key) {
     return mapper.getByKey(key);
  }

  public List<Configuration> getAll(){
    return mapper.getAll();
  }

  public void setValue(Configuration config){
    mapper.updateValue(config);
  }
}
