/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Refrigerator;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RefrigeratorMapper maps the Refrigerator entity to corresponding representation in database. Apart from basic CRUD operations
 * provides methods like getting all refrigerators in a delivery zone for a program.
 */
@Repository
public interface RefrigeratorMapper {

  @Insert({"INSERT INTO refrigerators",
    "(brand, model, serialNumber, facilityId, enabled, createdBy, modifiedBy)",
    "VALUES",
    "(#{brand}, #{model}, #{serialNumber}, #{facilityId}, #{enabled}, #{createdBy}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  void insert(Refrigerator refrigerator);

  @Select({"SELECT RF.*",
    "FROM facilities F INNER JOIN delivery_zone_members DZM ON F.id = DZM.facilityId",
    "INNER JOIN programs_supported PS ON PS.facilityId = F.id",
    "INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
    "INNER JOIN delivery_zone_program_schedules DZPS ON DZPS.deliveryZoneId = DZM.deliveryZoneId",
    "INNER JOIN refrigerators RF ON RF.facilityId = F.id",
    "WHERE DZPS.programId = #{programId} AND RF.enabled = true",
    "AND PS.programId = #{programId}  AND DZM.deliveryZoneId = #{deliveryZoneId} order by F.name, RF.serialNumber"})
  List<Refrigerator> getRefrigeratorsForADeliveryZoneAndProgram(@Param("deliveryZoneId") Long deliveryZoneId, @Param("programId") Long programId);

  @Update({"UPDATE refrigerators SET brand = #{brand}, model = #{model}, enabled = #{enabled}, modifiedBy = #{modifiedBy}, modifiedDate = DEFAULT WHERE id = #{id}"})
  void update(Refrigerator refrigerator);

  @Select({"SELECT * FROM refrigerators WHERE facilityId = #{facilityId}"})
  List<Refrigerator> getAllBy(Long facilityId);

  @Update({"UPDATE refrigerators SET enabled = false WHERE facilityId = #{facilityId}"})
  void disableAllFor(Long facilityId);
}
