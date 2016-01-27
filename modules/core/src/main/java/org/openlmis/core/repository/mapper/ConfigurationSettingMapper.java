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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ConfigurationSetting;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationSettingMapper {

  // Used by mapper
  @Select("SELECT * FROM configuration_settings WHERE LOWER(key) = LOWER(#{key})")
  ConfigurationSetting getByKey(String key);

  @Select("SELECT * FROM configuration_settings where isConfigurable = true order by groupName, displayOrder, name")
  List<ConfigurationSetting> getAll();


  @Select("SELECT * FROM configuration_settings where key like #{searchString} order by groupName, displayOrder, name")
  List<ConfigurationSetting> getSearchResults(@Param("searchString")String searchString);

  @Update("UPDATE configuration_settings set value = #{value} where KEY = #{key} ")
  void updateValue(ConfigurationSetting config );
  
}



