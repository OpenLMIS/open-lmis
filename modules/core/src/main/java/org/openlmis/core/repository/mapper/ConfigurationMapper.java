package org.openlmis.core.repository.mapper;


import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationMapper {
  @Select("SELECT * FROM configurations")
  public Configuration getConfiguration();
}