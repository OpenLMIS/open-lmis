/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.springframework.stereotype.Repository;

/**
 * DeliveryZoneMemberMapper maps the DeliveryZoneMember entity to corresponding representation in database. Apart from basic CRUD
 * operations provides methods to get programs for a delivery zone and facility.
 */
@Repository
public interface DeliveryZoneWarehouseMapper {

  @Insert({"INSERT INTO delivery_zone_warehouses(deliveryZoneId, warehouseId, createdBy, modifiedBy, modifiedDate)",
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
