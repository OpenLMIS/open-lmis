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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.report.OrderFillRateSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderFillRateSummaryListMapper {

    @Select(" WITH query as (\n" +
            "            SELECT DISTINCT facilityId,zonename district, facilityname facility, ft.name facilityType,\n" +
            "             CASE WHEN SUM(totalproductsapproved)=0 THEN 0 ELSE ROUND(count(CASE WHEN totalproductsreceived>0 THEN 1 ELSE NULL END) * 100/\n" +
            "             count(CASE WHEN totalproductsapproved>0 THEN 1 ELSE NULL END),0) END Order_fill_rate\n" +
            "             FROM vw_order_fill_rate\n" +
            "             JOIN facility_types ft on facilityTypeID=ft.id\n" +
            "             JOIN vw_districts d on d.district_id = zoneId \n" +
            "  WHERE  scheduleId= #{scheduleId} and programid=#{programId} and periodId=#{periodId} and (ft.id=#{facilityTypeId} or #{facilityTypeId} = 0) and totalproductsapproved>0\n" +
            " and facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{programId}) and \n" +
            " (d.district_id = #{zoneId} or d.zone_id = #{zoneId} or d.region_id = #{zoneId} or d.parent = #{zoneId} or #{zoneId} = 0 )" + "            " +
            "GROUP BY facilityId,zonename,facilityname,ft.name,totalproductsapproved\n" +
            "            )\n" +
            "            select Y.* from(\n" +
            "            select facilityId,district,facility,facilityType,Order_fill_rate,'A' as status from query \n" +
            "            where order_fill_rate between 75 and 100\n" +
            "            \n" +
            "            UNION ALL\n" +
            "             select facilityId,district,facility,facilityType,Order_fill_rate,'M' as status from query \n" +
            "             where order_fill_rate between 50 and 74.9\n" +
            "            UNION ALL\n" +
            "             select facilityId,district,facility,facilityType,Order_fill_rate,'L' as status from query \n" +
            "             where order_fill_rate between 1 and 49.9\n" +
            "            )Y\n" +
            "           where status=#{status}\n")
    public List<OrderFillRateSummaryReport> getOrderFillRateSummaryReportData(@Param("programId") Long programId,
                                                                              @Param("periodId") Long periodId,
                                                                              @Param("scheduleId") Long scheduleId,
                                                                              @Param("facilityTypeId") Long facilityTypeId,
                                                                              @Param("userId") Long userId,
                                                                              @Param("zoneId") Long zoneId,
                                                                              @Param("status") String status);


}
