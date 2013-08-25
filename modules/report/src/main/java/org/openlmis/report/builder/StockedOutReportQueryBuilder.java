/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.filter.StockedOutReportFilter;
import org.openlmis.report.model.report.StockedOutReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/* Date: 4/11/13
* Time: 11:34 AM
*/
public class StockedOutReportQueryBuilder {

    public static String getQuery(Map params){


        StockedOutReportFilter filter  = (StockedOutReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("DISTINCT supplyingfacility,facilitycode, facility, product, facilitytypename,  location");
        FROM("vw_stock_status");
        WHERE("status = 'SO'");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, StockedOutReport.class,"supplyingfacility asc, facility asc, product asc"));
        return SQL();

    }
    private static void writePredicates(StockedOutReportFilter filter){

        if(filter != null){
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if(filter.getFacility() != null && !filter.getFacility().isEmpty()){
                WHERE("facility = #{filterCriteria.facility}");
            }
            if (filter.getStartDate() != null) {
                WHERE("startdate >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("enddate <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("categoryid = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("rgid = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != 0){
                WHERE("productid= #{filterCriteria.productId}");
            } else {
                WHERE("indicator_product = true");
            }
        }
    }

    public static String getTotalCount(Map params){

        StockedOutReportFilter filter  = (StockedOutReportFilter)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) facilityCount");
        FROM("vw_stock_status");
        writePredicates(filter);
        return SQL();
    }

}
