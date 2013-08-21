package org.openlmis.core.repository.mapper;


import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationMapper {

  @Select("SELECT * FROM configurations")
  public Configuration getConfiguration();

  @Update("UPDATE configurations SET orderFilePrefix = #{orderFilePrefix}, headerInOrderFile = #{headerInOrderFile}, " +
    "orderDatePattern = #{orderDatePattern}, periodDatePattern = #{periodDatePattern}, modifiedBy = #{modifiedBy}, " +
    "modifiedDate = COALESCE(#{modifiedDate}, NOW())")
  void update(Configuration configuration);

}
