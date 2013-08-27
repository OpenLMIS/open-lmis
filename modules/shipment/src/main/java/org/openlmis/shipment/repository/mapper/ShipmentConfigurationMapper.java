package org.openlmis.shipment.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentConfigurationMapper {

  @Select("SELECT * FROM shipment_configuration")
  ShipmentConfiguration get();

  @Update({"UPDATE shipment_configuration set headerInFile =  #{headerInFile}, packedDatePattern= #{packedDatePattern}," ,
    "shippedDatePattern = #{shippedDatePattern}"})
  void update(ShipmentConfiguration shipmentConfiguration);

}
