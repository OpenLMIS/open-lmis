
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
import org.openlmis.report.model.report.TimelinessReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelinessStatusReportMapper {


    @Select("SELECT f.id facilityId,unschedled.facilitytypename facilityTypeName, f.code facilityCode,f.name facilityName, unschedled.STATUS, unschedled.CREATEDDATE duration,unschedled.reportingStatus  from \n" +
            "facilities f\n" +
            "left join (\n" +
            "select facilityId ,facilitytypename, createddate,status,reportingstatus reportingStatus from vw_timeliness_report\n" +
            "where\n" +
            " programId = #{programId} and periodId=#{periodId} AND scheduleId=#{scheduleId} and reportingstatus = #{status} and geographiczoneId = #{zoneId}   " +
            "GROUP BY facilityId,createddate,status,facilitytypename,reportingstatus\n" +

            ")unschedled ON f.id = unschedled.facilityId \n" +
            "\n" +
            "WHERE STATUS IS NOT NULL")
    public List<TimelinessReport> getTimelinessStatusData(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("scheduleId") Long scheduleId,
                                                          @Param("zoneId") Long zoneId, @Param("status") String status);


    @Select("select * from fn_getTimelinessReportData(#{programId}::int,#{zoneId}::int,#{periodId}::int,#{scheduleId}::int,#{status},#{facilityIds}) ")
    public List<TimelinessReport> getFacilityRnRStatusData(@Param("programId") Long programId, @Param("periodId") Long periodId, @Param("scheduleId") Long scheduleId,
                                                           @Param("zoneId") Long zoneId, @Param("status") String status, @Param("facilityIds") String facilityIds);
    @Select("select * from fn_get_timeliness_Reporting_Dates(#{periodId}::int) ")
    public List<TimelinessReport>getTimelinessReportingDates(@Param("periodId") Long periodId);

}
