/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.GeoZoneReportingRate;
import org.openlmis.report.model.dto.FlatGeographicZone;
import org.openlmis.report.model.dto.GeographicZone;
import org.openlmis.report.model.geo.GeoFacilityIndicator;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneReportMapper {

  @Select("SELECT g.* , p.id as parentId" +
    "   FROM " +
    "       geographic_zones g left join geographic_zones p on g.parentId = p.id order by p.name, g.name")
  List<GeographicZone> getAll();

  @Select("SELECT * FROM geographic_zones gz INNER JOIN geographic_levels gl ON gz.levelId = gl.id " +
    "  where levelId = #{geographicLevelId} ORDER BY gz.id,gl.id")
  List<GeographicZone> getGeographicZoneByLevel(Long id);

  @Select("select gz2.name ADM1,gz1.name ADM2, gz.name ADM3, gz.* from geographic_zones gz " +
        "left join geographic_zones gz1  " +
        "   on gz.parentId = gz1.id " +
        " left join geographic_zones gz2 " +
        "   on gz1.parentId = gz2.id" +
    " order by ADM1, ADM2, ADM3")
  List<FlatGeographicZone> getFlatGeographicZoneList();

  // TODO: refactor this for simplicity,
  // most of this query should move to a respective view.
  @Select("select gzz.id, gzz.name, gjson.geometry,COALESCE(expected.count) expected, COALESCE(total.count) total, COALESCE(ever.count,0) as ever, COALESCE(period.count,0) as period  " +
    " from  " +
        " geographic_zones gzz " +
      " left join  " +
         " geographic_zone_geojson gjson on  " +
          " gzz.id = gjson.zoneId " +
      " left join " +
      " (select geographicZoneId, count(*) from facilities  " +
      " join programs_supported ps on ps.facilityId = facilities.id " +
      " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
      " join requisition_group_members rgm on rgm.facilityId = facilities.id " +
      " join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and rgps.programId = ps.programId  " +
      " join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = #{processingPeriodId}  " +
      " where gz.levelId = (select max(id) from geographic_levels) and ps.programId = #{programId} " +
      " group by geographicZoneId" +
      " ) expected " +
      " on gzz.id = expected.geographicZoneId " +
      " left join " +
        " (select geographicZoneId, count(*) from facilities  " +
          " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
          " where gz.levelId = (select max(id) from geographic_levels)  " +
          " group by geographicZoneId" +
        " ) total " +
          " on gzz.id = total.geographicZoneId " +
    " left join  " +
        " (select geographicZoneId, count(*) from facilities  " +
        " join programs_supported ps on ps.facilityId = facilities.id " +
        " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
        " where ps.programId = #{programId} and facilities.id in  " +
          "(select facilityId from requisitions where programId = #{programId} ) " +
            "group by geographicZoneId" +
        " ) ever " +
        " on gzz.id = ever.geographicZoneId " +
     " left join " +
         " (select geographicZoneId, count(*) from facilities  " +
             " join programs_supported ps on ps.facilityId = facilities.id " +
             " join geographic_zones gz on gz.id = facilities.geographicZoneId " +
             " where  ps.programId = #{programId} and facilities.id in  " +
             " (select facilityId from requisitions where periodId = #{processingPeriodId} and programId = #{programId}) " +
              " group by geographicZoneId" +
    " ) period" +
    " on gzz.id = period.geographicZoneId order by gzz.name" )
  List<GeoZoneReportingRate> getGeoReportingRate(@Param("programId") Long programId, @Param("processingPeriodId") Long processingPeriodId);

  @Select("select f.id, f.name, f.mainPhone, f.longitude, f.latitude, false reported , (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts from facilities f\n" +
            "  join requisition_group_members m on f.id = m.facilityId\n" +
            "  join requisition_group_program_schedules s on s.requisitionGroupId = m.requisitionGroupId and s.programId = #{programId}\n" +
            "  join processing_periods pp on pp.scheduleId = s.scheduleId and pp.id = #{periodId}\n" +
            "where f.id not in (select facilityId from requisitions r where r.programId = #{programId} and r.periodId = #{periodId}) \n" +
            "  and f.enabled = true\n" +
            "  and f.geographicZoneId = #{geographicZoneId}" +
            " order by f.name ")
  List<GeoFacilityIndicator> getNonReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);

  @Select("select f.id, f.name, f.mainPhone, f.longitude, f.latitude, true reported, (select count(*) > 0 from users where users.active = true and users.facilityId = f.id) as hasContacts from facilities f\n" +
    "  join requisition_group_members m on f.id = m.facilityId\n" +
    "  join requisition_group_program_schedules s on s.requisitionGroupId = m.requisitionGroupId and s.programId = #{programId}\n" +
    "  join processing_periods pp on pp.scheduleId = s.scheduleId and pp.id = #{periodId}\n" +
    "where f.id in (select facilityId from requisitions r where r.programId = #{programId} and r.periodId = #{periodId}) \n" +
    "  and f.enabled = true\n" +
    "  and f.geographicZoneId = #{geographicZoneId}" +
    " order by f.name")
  List<GeoFacilityIndicator> getReportingFacilities(@Param("programId") Long programId, @Param("geographicZoneId") Long geographicZoneId, @Param("periodId") Long processingPeriodId);

    @Select("select gz2.id,  gz2.name msdZoneName\n" +
            "from geographic_zones gz\n" +
            "        left join geographic_zones gz1  \n" +
            "          on gz.parentId = gz1.id \n" +
            "         left join geographic_zones gz2 \n" +
            "           on gz1.parentId = gz2.id\n" +
            "         join facilities f on f.geographiczoneid =gz.id\n" +
            "           where gz1.parentId  is NOT NULL and gz2.parentId is NOT NULL\n" +
            "               group by gz2.name,gz2.id  order by msdZoneName ")
    List<GeographicZone>getAllMSDZones();

}
