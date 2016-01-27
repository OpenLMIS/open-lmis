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

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.dto.Facility;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface FacilityLookupReportMapper {

    @Select("SELECT *" +
            "   FROM " +
            "       facilities order by name")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    List<Facility> getAll(@Param("RowBounds") RowBounds rowBounds);

    @Select("SELECT * " +
            "   FROM " +
            "       facilities where code = #{code}")
    Facility getFacilityByCode(@Param("code") String code);

    @Select("SELECT f.id, f.code, f.name" +
            "   FROM " +
            "       facilities f " +
            "             join programs_supported ps " +
            "                   on ps.facilityid = f.id " +
            "             join vw_districts d on d.district_id = f.geographicZoneId " +
            "          where " +
            "               f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{program}) and " +
            "               (d.district_id = #{zone} or d.zone_id = #{zone} or d.region_id = #{zone} or d.parent = #{zone} or #{zone} = 0 ) and " +
            "               ps.programid = #{program} and ps.active = true  order by f.name")
    List<Facility> getFacilitiesByProgram(@Param("program") Long program, @Param("zone") Long zone, @Param("userId") Long userId);

    @Select("SELECT f.id, f.code, f.name" +
            "   FROM " +
            "       facilities f  " +
            "             join vw_districts d on d.district_id = f.geographicZoneId " +
            "          join programs_supported ps \n" +
            "            on ps.facilityid = f.id\n" +
            "          join requisition_group_members m \n" +
            "            on m.facilityId = f.id\n" +
            "          join requisition_group_program_schedules rps\n" +
            "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId " +
            "        where " +
            "               f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{program}) and " +
            "               (d.district_id = #{zone} or d.zone_id = #{zone} or d.region_id = #{zone} or d.parent = #{zone} or #{zone} = 0 ) and " +
            "             ps.programid = #{program} " +
            "             and rps.scheduleid = #{schedule} " +
            "             and ps.active = true  " +
            "        order by f.name")
    List<Facility> getFacilitiesByProgramSchedule(@Param("program") Long program, @Param("schedule") Long schedule, @Param("zone") Long zone, @Param("userId") Long userId);

    @Select("SELECT f.id, f.code, f.name" +
            "   FROM " +
            "       facilities f  " +
            "             join vw_districts d on d.district_id = f.geographicZoneId " +
            "          join programs_supported ps \n" +
            "            on ps.facilityid = f.id\n" +
            "          join requisition_group_members m \n" +
            "            on m.facilityId = f.id\n" +
            "          join requisition_group_program_schedules rps\n" +
            "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId " +
            "        where " +
            "               f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{program}) and " +
            "               (d.district_id = #{zone} or d.zone_id = #{zone} or d.region_id = #{zone} or d.parent = #{zone} or #{zone} = 0 ) and " +
            "             ps.programid = #{program} " +
            "             and rps.scheduleid = #{schedule} " +
            "             and ps.active = true  " +
            "             and f.id in (select facilityId from requisition_group_members where requisitionGroupId = #{requisitionGroup})  " +
            "        order by f.name")
    List<Facility> getFacilitiesByProgramScheduleAndRG(@Param("program") Long program, @Param("schedule") Long schedule, @Param("requisitionGroup") Long requisitionGroup, @Param("zone") Long zone, @Param("userId") Long userId);


    @Select("SELECT f.id, f.code, f.name" +
            "   FROM " +
            "       facilities f " +
            "             join vw_districts d on d.district_id = f.geographicZoneId " +
            "          join programs_supported ps \n" +
            "            on ps.facilityid = f.id\n" +
            "          join requisition_group_members m \n" +
            "            on m.facilityId = f.id\n" +
            "          join requisition_group_program_schedules rps\n" +
            "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
            "        where " +
            "               f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{program}) and" +
            "               (d.district_id = #{zone} or d.zone_id = #{zone} or d.region_id = #{zone} or d.parent = #{zone} or #{zone} = 0 ) and " +
            "             ps.programid = #{program} " +
            "             and rps.scheduleid = #{schedule} " +
            "             and f.typeid = #{type} " +
            "             and ps.active = true  " +
            "        order by f.name")
    List<Facility> getFacilitiesByPrgraomScheduleType(@Param("program") Long program, @Param("schedule") Long schedule, @Param("type") Long type, @Param("zone") Long zone, @Param("userId") Long userId);

    @Select("SELECT f.id, f.code, f.name" +
            "   FROM " +
            "       facilities f " +
            "             join vw_districts d on d.district_id = f.geographicZoneId " +
            "          join programs_supported ps \n" +
            "            on ps.facilityid = f.id\n" +
            "          join requisition_group_members m \n" +
            "            on m.facilityId = f.id\n" +
            "          join requisition_group_program_schedules rps\n" +
            "            on m.requisitionGroupId = rps.requisitionGroupId and ps.programId = rps.programId\n" +
            "        where " +
            "               f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{Program}) and " +
            "               (d.district_id = #{zone} or d.zone_id = #{zone} or d.region_id = #{zone} or d.parent = #{zone} or #{zone} = 0 ) and " +
            "             ps.programid = #{program} " +
            "             and rps.scheduleid = #{schedule} " +
            "             and f.typeid = #{type} " +
            "             and ps.active = true  " +
            "             and f.id in (select facilityid from requisition_group_members where requisitionGroupId = #{requisitionGroup})  " +
            "        order by f.name")
    List<Facility> getFacilitiesByPrgraomScheduleTypeAndRG(@Param("program") Long program, @Param("schedule") Long schedule, @Param("type") Long type, @Param("requisitionGroup") Long requisitionGroup, @Param("zone") Long zone);


    @Select("SELECT DISTINCT f.id, f.code, f.name\n" +
            "FROM facilities f\n" +
            "INNER JOIN programs_supported ps on f.id = ps.facilityId\n" +
            "INNER JOIN requisition_group_members rgm ON f.id = rgm.facilityId\n" +
            "INNER JOIN requisition_group_program_schedules rgps ON (rgps.requisitionGroupId = rgm.requisitionGroupId and ps.programid = rgps.programid)\n" +
            "INNER JOIN requisition_groups rg ON rg.id = rgm.requisitionGroupId\n" +
            "INNER JOIN vw_user_supervisorynodes sn ON sn.id = rg.supervisoryNodeId and ps.programId = sn.programId \n" +
            "INNER JOIN programs p ON p.id = ps.programId\n" +
            "INNER JOIN processing_schedules psc ON psc.id = rgps.scheduleId\n" +
            "WHERE ps.programId = CASE WHEN COALESCE(#{programId},0) = 0 THEN ps.programId ELSE #{programId} END\n" +
            "    AND rgps.scheduleId = CASE WHEN COALESCE(#{scheduleId},0) = 0 THEN rgps.scheduleId ELSE #{scheduleId} END\n" +
            "    AND CASE WHEN COALESCE(#{supervisoryNodeId},0) = 0 THEN sn.id = sn.id ELSE (sn.id = #{supervisoryNodeId} OR sn.parentId = #{supervisoryNodeId}) END\n" +
            "    AND CASE WHEN #{rgroupId} ='{}' THEN rg.id = rg.id ELSE rg.id =  ANY( #{rgroupId}::int[]) END\n" +
            "    AND sn.userId = #{userId}\n" +
            "    AND f.active = TRUE \n" +
            "    AND ps.active = TRUE\n" +
            "    AND f.virtualFacility = FALSE\n" +
            "UNION\n" +
            "SELECT f.id,f.code,f.name \n" +
            "FROM users U, facilities F \n" +
            "WHERE U.facilityId = F.id AND U.id = #{userId} AND f.active = TRUE AND f.virtualFacility = FALSE\n" +
            "order by name")
    List<Facility> getFacilitiesBy(@Param("userId") Long userId, @Param("supervisoryNodeId") Long supervisoryNodeId, @Param("rgroupId") String requisitionGroupId, @Param("programId") Long programId, @Param("scheduleId") Long scheduleId);

    @Select("SELECT DISTINCT  U.id userId, U.primarynotificationmethod, f.id facilityId, f.code, f.name, U.cellPhone  as phoneNumber,U.email email\n" +
            "FROM facilities f\n" +
            "LEFT OUTER JOIN Users U ON U.facilityId =  f.id\n" +
            "WHERE geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "AND f.active = TRUE \n" +
            "AND f.virtualFacility = FALSE\n" +
            "order by phonenumber,email,name")
    List<HashMap> getFacilitiesForNotifications(@Param("userId") Long userId, @Param("zoneId") Long zoneId);

    @Select("SELECT facilities.id, facilities.code, facilities.name\n" +
            "FROM facilities\n" +
            "join programs_supported ps on ps.facilityid = facilities.id\n" +
            "WHERE geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "and programid =#{programId}")
    List<Facility> getFacilitiesByGeographicZoneTree(@Param("userId") Long userId, @Param("zoneId") Long zoneId, @Param("programId") Long programId);

        @Select("SELECT DISTINCT facilities.id, facilities.code, facilities.name\n" +
                "FROM facilities\n" +
                "join programs_supported ps on ps.facilityid = facilities.id\n" +
                "WHERE geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int)) \n" +
                "order by facilities.name asc")
        List<Facility> getFacilitiesByGeographicZone(@Param("userId") Long userId, @Param("zoneId") Long zoneId);

    @Select("SELECT f.id, f.code, f.name \n" +
            "FROM  \n" +
            "facilities f  \n" +
            "     join programs_supported ps  \n" +
            "\t   on ps.facilityid = f.id  \n" +
            "     join vw_districts d on d.district_id = f.geographicZoneId  \n" +
            "  where  \n" +
            "       f.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{program}) and  \n" +
            "       (d.district_id = #{zone} or d.zone_id = #{zone} or d.region_id = #{zone} or d.parent = #{zone} or #{zone} = 0 ) and  \n" +
            "       ps.programid = #{program} and f.typeid = #{type} and ps.active = true  order by f.name")
    List<Facility> getFacilitiesByProgramZoneFacilityType(@Param("program") Long program, @Param("zone") Long zone, @Param("userId") Long userId, @Param("type") Long type);

    @Select("SELECT f.id, f.code, f.name  FROM facilities f " +
            "   where f.typeid = #{type} " +
            "        order by f.name")
    List<Facility> getFacilitiesBytype(Long type);
}
