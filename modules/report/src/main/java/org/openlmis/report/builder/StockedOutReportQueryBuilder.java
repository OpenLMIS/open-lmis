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

import org.openlmis.report.model.params.StockedOutReportParam;
import org.openlmis.report.model.report.StockedOutReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;


public class StockedOutReportQueryBuilder {

    public static String getQuery(Map params){

        StockedOutReportParam filter  = (StockedOutReportParam)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("DISTINCT supplyingfacility,facilitycode, facility, product, facilitytypename, location, processing_period_name");
        FROM("vw_stock_status");
        WHERE("status = 'SO'" );
        WHERE("reported_figures > 0");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, StockedOutReport.class,"supplyingfacility asc, facility asc, product asc, processing_period_name asc"));
        // copy the sql over to a variable, this makes the debugging much more possible.
        String sql = SQL();
        return sql;


    }
    private static void writePredicates(StockedOutReportParam filter){
        WHERE("req_status in ('APPROVED','RELEASED')");
        if(filter != null){
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
                WHERE("gz_id = #{filterCriteria.zoneId}");
            }

            WHERE("periodId = #{filterCriteria.periodId}");

            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("categoryid = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("rgid = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() > 0){
                WHERE("productid= #{filterCriteria.productId}");
            } else if (filter.getProductId() == 0) {
                WHERE("indicator_product = true");
            }
            if(filter.getProgramId() != 0 && filter.getProgramId() != -1){
                 WHERE("programid = #{filterCriteria.programId}");
            }
            if(filter.getFacilityId() != 0 && filter.getFacilityId() != -1){
                WHERE("facility_id = #{filterCriteria.facilityId}");
            }
        }
    }

    public static String getTotalFacilities(Map params){

        StockedOutReportParam filter  = (StockedOutReportParam)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) facilityCount");
        FROM("vw_stock_status");
        writePredicates(filter);
        return SQL();
    }

    public static String getTotalStockedoutFacilities(Map params){

        StockedOutReportParam filter  = (StockedOutReportParam)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) facilityCount");
        FROM("vw_stock_status");
        WHERE("status = 'SO'");
        WHERE("reported_figures > 0");
        writePredicates(filter);
        return SQL();
    }




}
