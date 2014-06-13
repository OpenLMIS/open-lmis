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

    @Select("select facilitycode,facilityname,facilitytypename,createddate,status  from vw_rnr_status_details \n" +
            "\n" +
            "             where requisitiongroupid = #{requisitionGroupId} and programid=#{programId} and periodid=#{periodId} and status= #{status}\n" +
            "                        group by facilitycode,facilityname,facilitytypename,createddate,status order by status\n" +
            "             ")
    List<RnRStatusSummaryReport> getRnRStatusDetail(@Param("periodId") Long periodId, @Param("programId") Long programId, @Param("requisitionGroupId") Long requisitionGroupId, @Param("status") String status);




    @Select("select status, count(rnrid) totalStatus from vw_rnr_status\n" +
            "where requisitiongroupid = #{requisitionGroupId} and periodid = #{periodId} group by status order by status")
    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriod(@Param("requisitionGroupId") Long requisitionGroupId,@Param("periodId") Long periodId,
                                                                               @Param("programId") Long programId);

    @Select("select programname, status, count(rnrid) totalStatus from vw_rnr_status" +
            "where  requisitiongroupid = #{requisitiongroupId} and periodid = #{periodId} group by programname, status")
    public List<RnRStatusSummaryReport>getRnRStatusByRequisitionGroupAndPeriodData(@Param("requisitionGroupId") Long requisitionGroupId,@Param("periodId") Long periodId);
}
