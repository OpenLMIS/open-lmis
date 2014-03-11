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
import org.openlmis.report.model.GeoReportData;
import org.openlmis.report.model.dto.FlatGeographicZone;
import org.openlmis.report.model.dto.GeographicZone;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeographicZoneReportMapper {

  @Select("SELECT g.id, g.code , g.name, p.name as parent" +
    "   FROM " +
    "       geographic_zones g left join geographic_zones p on g.parentid = p.id order by p.name, g.name")
  List<GeographicZone> getAll();

  @Select("SELECT * FROM geographic_zones gz INNER JOIN geographic_levels gl ON gz.levelid = gl.id\n" +
    "  where levelid = #{geographicLevelId} ORDER BY gz.id,gl.id")
  List<GeographicZone> getGeographicZoneByLevel(Long id);

  @Select("select gz2.name ADM1,gz1.name ADM2, gz.name ADM3, gz.* from geographic_zones gz " +
        "left join geographic_zones gz1  " +
        "   on gz.parentid = gz1.id " +
        " left join geographic_zones gz2 " +
        "   on gz1.parentid = gz2.id" +
    " order by ADM1, ADM2, ADM3")
  List<FlatGeographicZone> getFlatGeographicZoneList();

  @Select("select gzz.id, gzz.name, gjson.geometry,COALESCE(expected.count) expected, COALESCE(total.count) total, COALESCE(ever.count,0) as ever, COALESCE(period.count,0) as period  " +
    " from \n" +
        " geographic_zones gzz\n" +
      " left join \n" +
         " geographic_zone_geojson gjson on \n" +
          " gzz.id = gjson.zoneid\n" +

      " left join\n" +
      " (select geographiczoneid, count(*) from facilities \n" +
      " join programs_supported ps on ps.facilityid = facilities.id\n" +
      " join geographic_zones gz on gz.id = facilities.geographiczoneid\n" +
      " join requisition_group_members rgm on rgm.facilityid = facilities.id\n" +
      " join requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and rgps.programid = ps.programid \n" +
      " join processing_periods pp on pp.scheduleid = rgps.scheduleid and pp.id = #{processingPeriodId} \n" +
      " where gz.levelid = 4 and ps.programid = #{programId}\n" +
      " group by geographiczoneid" +
      " ) expected\n" +
      " on gzz.id =expected.geographiczoneid\n" +

      " left join\n" +
        " (select geographiczoneid, count(*) from facilities \n" +
          " join geographic_zones gz on gz.id = facilities.geographiczoneid\n" +
          " where gz.levelid = 4 \n" +
          " group by geographiczoneid" +
        " ) total\n" +
          " on gzz.id =total.geographiczoneid\n" +
    " left join \n" +
        " (select geographiczoneid, count(*) from facilities \n" +
        " join programs_supported ps on ps.facilityid = facilities.id\n" +
        " join geographic_zones gz on gz.id = facilities.geographiczoneid\n" +
        " where ps.programid = #{programId} and facilities.id in \n" +
          "(select facilityid from requisitions)\n" +
            "group by geographiczoneid" +
        " ) ever\n" +
        " on gzz.id = ever.geographiczoneid\n" +
     " left join\n" +
         " (select geographiczoneid, count(*) from facilities \n" +
             " join programs_supported ps on ps.facilityid = facilities.id\n" +
             " join geographic_zones gz on gz.id = facilities.geographiczoneid\n" +
             " where  ps.programid = #{programId} and facilities.id in \n" +
             " (select facilityid from requisitions where periodid = #{processingPeriodId})\n" +
              " group by geographiczoneid" +
    " ) period" +
    " on gzz.id = period.geographiczoneid" )
  List<GeoReportData> getGeoReportingRate(@Param("programId") Long programId, @Param("processingPeriodId") Long processingPeriodId);
}
