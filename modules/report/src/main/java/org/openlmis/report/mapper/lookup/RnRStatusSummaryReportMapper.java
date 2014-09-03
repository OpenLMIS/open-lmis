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
import org.openlmis.report.model.dto.RnRStatusSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RnRStatusSummaryReportMapper {


    @Select("select count(rnrid) totalStatus,status from vw_number_rnr_created where requisitiongroupid = #{requisitionGroupId} order by status")
    public  List<RnRStatusSummaryReport>getRnRStatusSummaryData(@Param("requisitionGroupId") Long requisitionGroupId);

    @Select("select facilitycode,facilityname,facilitytypename,createddate,status  from vw_rnr_status_details \n" +
            " where requisitiongroupid = #{requisitiongroupid} and programid=#{programId} and periodid=#{periodId}\n" +
            "            group by facilitycode,facilityname,facilitytypename,createddate,status order by facilityname ")
    public List<RnRStatusSummaryReport>getRnRStatusDetails(@Param("requisitionGroupId") Long requisitionGroupId,@Param("programId") Long programId,@Param("periodId") Long periodId);

    @Select("SELECT\n" +
            "programs.name AS programname,\n" +
            "programs.id AS programid,\n" +
            "vw_facility_requisitions.periodid,\n" +
            "processing_schedules.name AS periodname,\n" +
            "vw_facility_requisitions.geographiczoneid AS geographiczoneid,\n" +
            "vw_facility_requisitions.geographiczonename,\n" +
            "facility_types.name AS facilitytypename,\n" +
            "vw_facility_requisitions.facilityid,\n" +
            "vw_facility_requisitions.facilitycode,\n" +
            "vw_facility_requisitions.facilityname,\n" +
            "vw_facility_requisitions.rnrid,\n" +
            "vw_facility_requisitions.status,\n" +
            "vw_facility_requisitions.createddate\n" +
            "FROM\n" +
            "vw_facility_requisitions\n" +
            "INNER JOIN programs ON programs.id = vw_facility_requisitions.programid\n" +
            "INNER JOIN facility_types ON facility_types.id = vw_facility_requisitions.typeid\n" +
            "INNER JOIN processing_periods ON processing_periods.id = vw_facility_requisitions.periodid\n" +
            "INNER JOIN processing_schedules ON processing_schedules.id = processing_periods.scheduleid\n" +
            "WHERE vw_facility_requisitions.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "AND programid = #{programId}\n" +
            "AND periodid = #{periodId}\n"+
            "AND  status= #{status}\n" +
            "AND status in ('APPROVED','AUTHORIZED','IN_APPROVAL','RELEASED') ")
    List<RnRStatusSummaryReport> getRnRStatusDetail(@Param("userId") Long userId, @Param("periodId") Long periodId, @Param("programId") Long programId, @Param("zoneId") Long zoneId, @Param("status") String status);




    @Select("SELECT\n" +
            "vw_facility_requisitions.status,\n" +
            "count(*) totalStatus\n" +
            "FROM\n" +
            "vw_facility_requisitions\n" +
            "INNER JOIN programs ON programs.id = vw_facility_requisitions.programid\n" +
            "where vw_facility_requisitions.geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children(#{userId}::int,#{zoneId}::int))\n" +
            "and vw_facility_requisitions.programid = #{programId}\n" +
            "and vw_facility_requisitions.periodid = #{periodId}\n" +
            "and status in ('APPROVED','AUTHORIZED','IN_APPROVAL','RELEASED') " +
            "GROUP BY vw_facility_requisitions.status " +
            "order by status \n")
    public List<RnRStatusSummaryReport> getRnRStatusSummary(@Param("userId") Long userId, @Param("zoneId") Long zoneId, @Param("periodId") Long periodId,
                                                            @Param("programId") Long programId);

    @Select("select programname, status, count(rnrid) totalStatus from vw_rnr_status" +
            "where  requisitiongroupid = #{requisitiongroupId} and periodid = #{periodId} " +
            "and status in ('APPROVED','AUTHORIZED','IN_APPROVAL','RELEASED') " +
            "group by programname, status " +
            "order by status")
    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriodData(@Param("requisitionGroupId") Long requisitionGroupId,@Param("periodId") Long periodId);
}
