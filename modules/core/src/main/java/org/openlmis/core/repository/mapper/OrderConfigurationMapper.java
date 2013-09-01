package org.openlmis.core.repository.mapper;


import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.core.domain.OrderConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderConfigurationMapper {

  @Select("SELECT * FROM order_configuration")
  public OrderConfiguration get();

  @Update({"UPDATE order_configuration SET filePrefix = #{filePrefix}, headerInFile = #{headerInFile}, ",
    "modifiedBy = #{modifiedBy}, modifiedDate = COALESCE(#{modifiedDate}, NOW())"})
  void update(OrderConfiguration orderConfiguration);

}
