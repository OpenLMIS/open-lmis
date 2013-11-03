/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.ConfigurationSetting;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationSettingMapper {

  // Used by mapper
  @Select("SELECT * FROM configuration_settings WHERE LOWER(key) = LOWER(#{key})")
  ConfigurationSetting getByKey(String key);

  @Select("SELECT * FROM configuration_settings order by groupName, displayOrder, name")
  List<ConfigurationSetting> getAll();

  @Update("UPDATE configuration_settings set value = #{value} where KEY = #{key} ")
  void updateValue(ConfigurationSetting config );
  
}



