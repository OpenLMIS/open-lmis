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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.ConfigurationSetting;
import org.openlmis.core.repository.mapper.ConfigurationSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class ConfigurationSettingRepository {

  private ConfigurationSettingMapper mapper;

  @Autowired
  public ConfigurationSettingRepository(ConfigurationSettingMapper configurationSettingMapper) {
    this.mapper = configurationSettingMapper;
  }

  public ConfigurationSetting getByKey(String key) {
    return mapper.getByKey(key);
  }

  public List<ConfigurationSetting> getAll() {
    return mapper.getAll();
  }

  public void setValue(ConfigurationSetting config) {
    mapper.updateValue(config);
  }

  public List<ConfigurationSetting> getSearchResults(String s) {
    return mapper.getSearchResults(s);
  }
}
