package org.openlmis.allocation.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryZoneWarehouseMapper {

  @Insert({"INSERT INTO delivery_zone_warehouses(deliveryZoneId, warehouseId, createdBy, createdDate, modifiedDate)",
                          "VALUES(#{deliveryZone.id}, #{warehouse.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insert(DeliveryZoneWarehouse warehouse);

  @Update({"UPDATE delivery_zone_warehouses SET modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} WHERE id = #{id}"})
  void update(DeliveryZoneWarehouse warehouse);

  @Select({"SELECT DZW.* FROM delivery_zone_warehouses DZW INNER JOIN delivery_zones DZ ON DZ.id = DZW.deliveryZoneId",
      "INNER JOIN facilities F ON DZW.warehouseId = F.id WHERE F.code = #{warehouseCode} AND DZ.code = #{deliveryZoneCode}"})
  @Results({
      @Result(column = "deliveryZoneId", property = "deliveryZone.id"),
      @Result(column = "warehouseId", property = "warehouse.id")
  })
  DeliveryZoneWarehouse getByDeliveryZoneCodeAndWarehouseCode(@Param("deliveryZoneCode") String deliveryZoneCode,
                                                              @Param("warehouseCode") String warehouseCode);
}
