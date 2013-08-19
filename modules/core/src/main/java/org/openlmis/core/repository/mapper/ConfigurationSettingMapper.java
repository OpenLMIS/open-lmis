package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.Configuration;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationSettingMapper {
  @Select("SELECT * FROM configurations")
  public Configuration getConfiguration();

  // Used by mapper
  @Select("SELECT * FROM configurations WHERE LOWER(key) = LOWER(#{key})")
  Configuration getByKey(String key);

  @Select("SELECT * FROM configurations order by groupName, displayOrder, name")
  List<Configuration> getAll();

  @Update("UPDATE configurations set value = #{value} where KEY = #{key} ")
  void updateValue(Configuration config );
  
}



