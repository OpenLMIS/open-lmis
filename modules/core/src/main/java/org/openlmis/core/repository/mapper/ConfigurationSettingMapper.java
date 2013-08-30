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



