/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceLogMapper {


  @Select("select * from equipment_maintenance_logs where id = #{id}")
  MaintenanceLog getById(Long id);

  @Select("select * from equipment_maintenance_logs")
  List<MaintenanceLog> getAll();

  @Select("select * from equipment_maintenance_logs where facilityId = #{facilityId}")
  List<MaintenanceLog> getAllForFacility(@Param("facilityId") Long facilityId);

  @Select("select * from equipment_maintenance_logs where vendorId = #{vendorId}")
  List<MaintenanceLog> getAllForVendor(@Param("vendorId") Long vendorId);

  @Insert("insert into equipment_maintenance_logs (userId, facilityId, equipmentId, vendorId, contractId, maintenanceDate, servicePerformed, finding, recommendation, requestId, nextVisitDate, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      " (#{userId}, #{facilityId}, #{equipmentId}, #{vendorId}, #{contractId}, #{maintenanceDate}, #{servicePerformed}, #{finding}, #{recommendation}, #{requestId}, #{nextVisitDate} , #{createdBy},COALESCE(#{createdDate}, NOW()), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(MaintenanceLog value);

  @Update("UPDATE equipment_maintenance_logs SET " +
      "userId = #{userId}, facilityId = #{facilityId}, equipmentId = #{equipmentId}, vendorId = #{vendorId}, contractId = #{contractId}, maintenanceDate = #{maintenanceDate}, servicePerformed = #{servicePerformed}, finding = #{finding}, recommendation = #{recommendation}, requestId = #{requestId}, nextVisitDate = #{nextVisitDate}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " WHERE id = #{id}")
  void update(MaintenanceLog value);
}
