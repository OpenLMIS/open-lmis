/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.params.StockImbalanceReportParam;
import org.openlmis.report.model.report.StockImbalanceReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class StockImbalanceQueryBuilder {
    public static String getQuery(Map params){


        StockImbalanceReportParam filter  = (StockImbalanceReportParam)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("distinct supplyingfacility,  facility,  product,  stockinhand physicalCount,  amc,  mos months,  required orderQuantity, CASE WHEN status = 'SO' THEN  'Stocked Out' WHEN status ='US' then  'Below Minimum' WHEN status ='OS' then  'Over Stocked' END AS status ");
        FROM("vw_stock_status join facilities f on f.id = facility_id join vw_districts d on d.district_id = f.geographicZoneId ");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, StockImbalanceReport.class, "supplyingFacility asc, facility asc, product asc"));
        return SQL();

        }
    private static void writePredicates(StockImbalanceReportParam filter){
        WHERE("status <> 'SP'");
        WHERE("req_status in ('APPROVED','RELEASED')");
        WHERE("(amc != 0 or stockinhand != 0 )");
        WHERE("periodid = #{filterCriteria.periodId}");
        WHERE("psid = #{filterCriteria.scheduleId}");
        // apply user's permission
        WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
        if(filter != null){

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                WHERE("programid = #{filterCriteria.programId}");
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if(filter.getFacility() != null && !filter.getFacility().isEmpty() && !filter.getFacility().equals("0")){
                WHERE("facility_id = #{filterCriteria.facility}::numeric");
            }

            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("categoryid = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("rgid = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != 0 && filter.getProductId() != -1){
                WHERE("productid= #{filterCriteria.productId}");
            } else if (filter.getProductId() == -1){
                WHERE("indicator_product = true");
            }

            if(filter.getZoneId() > 0 ){
              WHERE("( d.district_id = #{filterCriteria.zoneId} or d.zone_id = #{filterCriteria.zoneId} or d.region_id = #{filterCriteria.zoneId} or d.parent = #{filterCriteria.zoneId} )");
            }
        }
    }
}
