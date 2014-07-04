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
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DeliveryZoneMapper maps the DeliveryZone entity to corresponding representation in database. Apart from basic CRUD
 * operations provides methods to get programs for a delivery zone, get delivery zones according to  user rights etc.
 */
@Repository
public interface DeliveryZoneMapper {

  @Insert({"INSERT INTO delivery_zones (code, name, description, createdBy, modifiedBy, modifiedDate)",
    "VALUES (#{code}, #{name}, #{description}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
  @Options(useGeneratedKeys = true)
  void insert(DeliveryZone zone);

  @Update({"UPDATE delivery_zones SET code = #{code}, name = #{name}, description = #{description}, modifiedBy = #{modifiedBy},",
    "modifiedDate = #{modifiedDate} WHERE id = #{id}"})
  void update(DeliveryZone zone);

  @Select({"SELECT * FROM delivery_zones WHERE id = #{id}"})
  DeliveryZone getById(Long id);

  @Select({"SELECT * FROM delivery_zones WHERE LOWER(code) = LOWER(#{code})"})
  DeliveryZone getByCode(String code);

  @Select({"SELECT DZ.* FROM delivery_zones DZ INNER JOIN role_assignments RA ON RA.deliveryZoneId = DZ.id",
    "INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
    "WHERE RR.rightName = #{right} AND RA.userId = #{userId}"})
  List<DeliveryZone> getByUserForRight(@Param("userId") long userId, @Param("right") String right);

  @Select({"SELECT programId as id FROM delivery_zone_program_schedules WHERE deliveryZoneId = #{id}"})
  List<Program> getPrograms(long id);

  @Select("SELECT * FROM delivery_zones")
  List<DeliveryZone> getAll();
}
