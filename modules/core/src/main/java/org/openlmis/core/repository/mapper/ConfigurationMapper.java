package org.openlmis.core.repository.mapper;


import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.OrderConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationMapper {

  @Select("SELECT * FROM order_configurations")
  public OrderConfiguration getConfiguration();

  @Update("UPDATE order_configurations SET filePrefix = #{filePrefix}, headerInFile = #{headerInFile}, " +
    "datePattern = #{datePattern}, periodDatePattern = #{periodDatePattern}, modifiedBy = #{modifiedBy}, " +
    "modifiedDate = COALESCE(#{modifiedDate}, NOW())")
  void update(OrderConfiguration orderConfiguration);

}
