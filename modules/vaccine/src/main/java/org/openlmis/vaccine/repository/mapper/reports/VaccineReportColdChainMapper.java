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

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.reports.AdverseEffectLineItem;
import org.openlmis.vaccine.domain.reports.ColdChainLineItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportColdChainMapper {


  @Insert("INSERT INTO vaccine_report_cold_chain_line_items " +
    "(reportId, equipmentInventoryId, minTemp, maxTemp, minEpisodeTemp, maxEpisodeTemp, remarks, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values " +
    "( #{reportId}, #{equipmentInventoryId}, #{minTemp}, #{maxTemp}, #{minEpisodeTemp}, #{maxEpisodeTemp}, #{remarks}, #{createdBy}, NOW(), #{modifiedBy}, NOW() )")
  @Options(useGeneratedKeys = true)
  void insert(ColdChainLineItem lineItem);

  @Update("UPDATE vaccine_report_cold_chain_line_items " +
    " SET" +
      " reportId = #{reportId}" +
      " , equipmentInventoryId = #{equipmentInventoryId}" +
      " , minTemp = #{minTemp}" +
      " , maxTemp = #{maxTemp} " +
      " , minEpisodeTemp = #{minEpisodeTemp}" +
      " , maxEpisodeTemp = #{maxEpisodeTemp}" +
      " , remarks = #{remarks}" +
      " , modifiedBy = #{modifiedBy}" +
      " , modifiedDate = NOW() " +
    " WHERE id = #{id}")
  void update(ColdChainLineItem lineItem);

  @Select("SELECT  i.id, eq.name as equipmentName, eq.model as model, e.serialNumber as serial, eq.energyTypeId, i.* " +
    " from " +
    " vaccine_report_cold_chain_line_items i " +
    "   join equipment_inventories e on e.id = i.equipmentInventoryId " +
    "   join equipments eq on eq.id = e.equipmentId " +
    " where " +
    " i.reportId = #{reportId} order by i.id")
  List<ColdChainLineItem> getLineItems(@Param("reportId") Long reportId);

  @Select("select e.id as equipmentInventoryId, eq.name as equipmentName, eq.model as model, e.serialNumber as serial, eq.energyTypeId " +
    " from " +
    "     equipment_inventories e " +
    "     join equipments eq on eq.id = e.equipmentId " +
    "   where" +
    "  e.programId = #{programId} and e.facilityId = #{facilityId} " +
    " order by eq.name")
  List<ColdChainLineItem> getNewEquipmentLineItems(@Param("programId")Long programId, @Param("facilityId") Long facilityId);
}
