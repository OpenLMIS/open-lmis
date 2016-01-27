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
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.dto.Log;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestMapper {

  @Select("select * from equipment_maintenance_requests where id = #{id}")
  MaintenanceRequest getById(Long id);

  @Select("select * from equipment_maintenance_requests")
  List<MaintenanceRequest> getAll();

  @Select("select * from equipment_maintenance_requests where facilityId = #{facilityId}")
  List<MaintenanceRequest> getAllForFacility(@Param("facilityId") Long facilityId);

  @Select("select * from equipment_maintenance_requests where vendorId = #{vendorId}")
  List<MaintenanceRequest> getAllForVendor(@Param("vendorId") Long vendorId);

  @Select("select * from equipment_maintenance_requests where vendorId = #{vendorId} and resolved = false")
  List<MaintenanceRequest> getOutstandingRequestsForVendor(@Param("vendorId") Long vendorId);

  @Select("select r.*, e.name as equipmentName, f.name as facilityName " +
      "from " +
      "  equipment_maintenance_requests r " +
      "  join facility_program_equipments i on i.id = r.inventoryId " +
      "  join equipments e on e.id = i.equipmentId " +
      "  join facilities f on f.id = r.facilityId " +
      " where " +
      "   r.vendorId in ( select vendorId from equipment_service_vendor_users where userId = #{userId}) " +
      "   and resolved = false " +
      " order by requestDate desc")
  List<MaintenanceRequest> getOutstandingRequestsForUser(@Param("userId") Long userId);


  @Insert("insert into equipment_maintenance_requests (userId, facilityId, inventoryId, vendorId, requestDate, reason, recommendedDate, comment, resolved, vendorComment, createdBy, createdDate, modifiedBy, modifiedDate) " +
      " values " +
      " (#{userId}, #{facilityId}, #{inventoryId}, #{vendorId}, #{requestDate}, #{reason}, #{recommendedDate}, #{comment}, #{resolved}, #{vendorComment} , #{createdBy},COALESCE(#{createdDate}, NOW()), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(MaintenanceRequest value);

  @Update("UPDATE equipment_maintenance_requests SET " +
      "userId = #{userId}, facilityId = #{facilityId}, inventoryId = #{inventoryId}, vendorId = #{vendorId}, requestDate = #{requestDate}, reason = #{reason}, recommendedDate = #{recommendedDate}, comment = #{comment}, resolved = #{resolved}, vendorComment = #{vendorComment}, modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " WHERE id = #{id}")
  void update(MaintenanceRequest value);

  @Select({"select users.username as who, r.reason, 'request' as type, r.resolved as status, r.comment, r.requestDate as date from equipment_maintenance_requests r join users on users.id = r.userId where inventoryId = #{inventoryId} ",
  " UNION ",
  " select v.name as who, r.reason, 'maintenance' as type, r.resolved as status, m.servicePerformed as comment, m.maintenanceDate as date from equipment_maintenance_logs m left join equipment_maintenance_requests r on m.requestId = r.id join equipment_service_vendors v on v.id = m.vendorId where inventoryId = #{inventoryId}"})
  List<Log> getFullHistory(@Param("inventoryId") Long inventoryId);
}
