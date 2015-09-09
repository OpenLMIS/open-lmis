/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.FacilityLevelTree;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityLevelMapper {

    @Select("SELECT DISTINCT  f.name facilityName,  f.id facilityId,  " +
            "FA.NAME superVisedFacility,FA.ID superVisedFacilityId, sN.parentId  " +
            "FROM facilities f  " +
            "INNER JOIN programs_supported ps ON f.id=ps.facilityId " +
            "INNER JOIN facility_types on  f.typeId = facility_types.id  " +
            "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId " +
            "INNER JOIN requisition_group_program_schedules rgps ON (rgps.programId = ps.programId AND rgps.requisitionGroupId=rgm.requisitionGroupId)" +
            "INNER JOIN REQUISITION_GROUPs rg ON RG.ID = RGM.REQUISITIONGROUPID  " +
            "INNER JOIN supervisory_nodes sN on sN.ID = RG.supervisoryNodeId " +
            "INNER JOIN facilities FA on sN.facilityId = FA.id  " +
            "WHERE ps.programId = #{programId}  AND facility_types.code IN ('cvs','rvs','dvs') " +
            "AND rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
            "AND rgps.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
            "AND f.active = TRUE " +
            "AND ps.active = TRUE " +
            "AND f.virtualFacility = FALSE  AND COALESCE(sN.parentId,0)>0  ")
    List<FacilityLevelTree> getFacilitiesByLevel(@Param(value = "programId") Long programId,
                                                 @Param(value = "requisitionGroupIds") String requisitionGroupIds);


    @Select("SELECT DISTINCT  f.name facilityName,  f.id facilityId,  " +
            "FA.ID superVisedFacilityId, sN.parentId  " +
            "FROM facilities f  " +
            "INNER JOIN programs_supported ps ON f.id=ps.facilityId " +
            "INNER JOIN facility_types on facility_types.id = f.typeId " +
            "INNER JOIN requisition_group_members rgm ON f.id= rgm.facilityId " +
            "INNER JOIN requisition_group_program_schedules rgps ON (rgps.programId = ps.programId AND rgps.requisitionGroupId=rgm.requisitionGroupId)" +
            "INNER JOIN REQUISITION_GROUPs rg ON RG.ID = RGM.REQUISITIONGROUPID  " +
            "INNER JOIN supervisory_nodes sN on sN.ID = RG.supervisoryNodeId " +
            "INNER JOIN facilities FA on sN.facilityId = FA.id  " +
            "WHERE ps.programId = #{programId} AND facility_types.code IN ('cvs','rvs','dvs') " +
            "AND rgm.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
            "AND rgps.requisitionGroupId = ANY(#{requisitionGroupIds}::INTEGER[]) " +
            "AND f.active = TRUE " +
            "AND ps.active = TRUE " +
            "AND f.virtualFacility = FALSE   ")
    List<FacilityLevelTree> getParentTree(@Param(value = "programId") Long programId,
                                          @Param(value = "requisitionGroupIds") String requisitionGroupIds);


}
