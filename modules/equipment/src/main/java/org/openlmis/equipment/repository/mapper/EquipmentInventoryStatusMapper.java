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
import org.openlmis.equipment.domain.EquipmentInventoryStatus;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentInventoryStatusMapper {

  @Select("SELECT eis.*" +
      " FROM equipment_inventory_statuses eis" +
      " WHERE eis.inventoryId = #{inventoryId}" +
      " ORDER BY eis.createddate DESC LIMIT 1")
  EquipmentInventoryStatus getCurrentStatus(@Param("inventoryId")Long inventoryId);

  @Insert("INSERT INTO equipment_inventory_statuses" +
      " (inventoryId, statusId, notFunctionalStatusId, createdBy, createdDate, modifiedBy, modifiedDate)" +
      " VALUES (#{inventoryId}, #{statusId}, #{notFunctionalStatusId}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  void insert(EquipmentInventoryStatus status);

  @Update("UPDATE equipment_inventory_statuses" +
      " SET inventoryId = #{inventoryId}, statusId = #{statusId}, notFunctionalStatusId = #{notFunctionalStatusId}," +
      " modifiedBy = #{modifiedBy}, modifiedDate = NOW()" +
      " WHERE id = #{id}")
  void update(EquipmentInventoryStatus status);
}
