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
import org.openlmis.core.domain.DeliveryZoneMember;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * DeliveryZoneMemberMapper maps the DeliveryZoneMember entity to corresponding representation in database. Apart from basic CRUD
 * operations provides methods to get programs for a delivery zone and facility.
 */
@Repository
public interface DeliveryZoneMemberMapper {

  @Insert({"INSERT INTO delivery_zone_members(deliveryZoneId, facilityId, createdBy, modifiedBy, modifiedDate)",
      "VALUES(#{deliveryZone.id}, #{facility.id}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insert(DeliveryZoneMember member);

  @Update("UPDATE delivery_zone_members SET modifiedBy = #{modifiedBy}, modifiedDate = #{modifiedDate} WHERE id = #{id}")
  void update(DeliveryZoneMember member);

  @Select({"SELECT DZM.* FROM delivery_zone_members DZM INNER JOIN delivery_zones DZ ON DZ.id = DZM.deliveryZoneId",
  "INNER JOIN facilities F ON DZM.facilityId = F.id WHERE F.code = #{facilityCode} AND DZ.code = #{deliveryZoneCode}"})
      @Results({
          @Result(column = "deliveryZoneId", property = "deliveryZone.id"),
          @Result(column = "facilityId", property = "facility.id")
      })
  DeliveryZoneMember getByDeliveryZoneCodeAndFacilityCode(@Param("deliveryZoneCode") String deliveryZoneCode,
                                                          @Param("facilityCode") String facilityCode);

  @Select({"SELECT DZPS.programId from delivery_zone_program_schedules DZPS",
      "INNER JOIN delivery_zone_members DZM ON DZM.deliveryZoneId = DZPS.deliveryZoneId AND DZM.facilityId = #{facilityId}"})
  List<Long> getDeliveryZoneProgramIdsForFacility(Long facilityId);

}
