/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.filter.StockImbalanceReportFilter;
import org.openlmis.report.model.report.StockImbalanceReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:40 PM
 */
public class StockImbalanceQueryBuilder {
    public static String getQuery(Map params){


        StockImbalanceReportFilter filter  = (StockImbalanceReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("distinct supplyingfacility,  facility,  product,  stockinhand physicalCount,  amc,  mos months,  required orderQuantity, CASE WHEN status = 'SO' THEN  'Stocked Out' WHEN status ='US' then  'Below Minimum' WHEN status ='OS' then  'Over Stocked' END AS status ");
        FROM("vw_stock_status");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, StockImbalanceReport.class, "supplyingfacility asc, facility asc, product asc"));
        return SQL();

        }
    private static void writePredicates(StockImbalanceReportFilter filter){
        WHERE("status <> 'SP'");
        WHERE("periodid = #{filterCriteria.periodId}");//required param
        WHERE("psid = #{filterCriteria.scheduleId}");//required param

        if(filter != null){

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if(filter.getFacility() != null && !filter.getFacility().isEmpty()){
                WHERE("facility = #{filterCriteria.facility}");
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
        }
    }
}
