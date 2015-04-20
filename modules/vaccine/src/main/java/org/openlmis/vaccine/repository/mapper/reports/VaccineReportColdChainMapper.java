/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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

  @Select("SELECT  i.id, eq.name as equipmentName, e.model as model, e.serialNumber as serial, e.energySource, i.* " +
    " from " +
    " vaccine_report_cold_chain_line_items i " +
    "   join equipment_inventories e on e.id = i.equipmentInventoryId " +
    "   join equipments eq on eq.id = e.equipmentId " +
    " where " +
    " i.reportId = #{reportId} order by i.id")
  List<ColdChainLineItem> getLineItems(@Param("reportId") Long reportId);

  @Select("select e.id as equipmentInventoryId, eq.name as equipmentName, e.model as model, e.serialNumber as serial, e.energySource " +
    " from " +
    "     equipment_inventories e " +
    "     join equipments eq on eq.id = e.equipmentId " +
    "   where" +
    "  e.programId = #{programId} and e.facilityId = #{facilityId} " +
    " order by eq.name")
  List<ColdChainLineItem> getNewEquipmentLineItems(@Param("programId")Long programId, @Param("facilityId") Long facilityId);
}
