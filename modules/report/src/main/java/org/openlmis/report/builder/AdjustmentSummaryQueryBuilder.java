/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.filter.AdjustmentSummaryReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;


public class AdjustmentSummaryQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){

        AdjustmentSummaryReportFilter filter  = (AdjustmentSummaryReportFilter)params.get("filterCriteria");
        Map<String, String[]> sorter = ( Map<String, String[]>)params.get("SortCriteria");
        BEGIN();

        SELECT("product productDescription, product_category_name category, facility_type_name facilityType,facility_name facilityName, adjustment_type adjustmentType, SUM(totallossesandadjustments) adjustment,supplying_facility_name supplyingFacility");
        FROM("vw_requisition_adjustment");
        writePredicates(filter);
        GROUP_BY("product, adjustment_type,product_category_name,facility_type_name,facility_name, supplying_facility_name");
        ORDER_BY(QueryHelpers.getSortOrder(params, "facility_type_name,facility_name, supplying_facility_name, product, product_category_name , adjustment_type"));
        String query = SQL();
        return SQL();
    }

    public static String SelectFilteredSortedPagedRecordsCount(Map params){

        AdjustmentSummaryReportFilter filter  = (AdjustmentSummaryReportFilter)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) perCounts");
        FROM(" vw_requisition_adjustment");
        writePredicates(filter);

        GROUP_BY("product, adjustment_type,product_category_name,facility_type_name,facility_name, supplying_facility_name");
        ORDER_BY(QueryHelpers.getSortOrder(params, "facility_type_name,facility_name, supplying_facility_name, product, product_category_name , adjustment_type"));

        String subQuery = SQL().toString();

        BEGIN();
        SELECT("COUNT(*)");
        FROM("( "+ subQuery +" ) as counts");
        return SQL();
    }

    private static void writePredicates(AdjustmentSummaryReportFilter filter){
        WHERE("req_status in ('APPROVED','RELEASED')");
        if(filter != null){
            if (filter.getFacilityTypeId() != 0) {
                WHERE("facility_type_id = #{filterCriteria.facilityTypeId}");
            }

            if (filter.getStartDate() != null) {
                WHERE("processing_periods_start_date >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("processing_periods_end_date <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 ){
                WHERE("product_category_id = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0){
                WHERE("requisition_group_id = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != -1 && filter.getProductId() != 0){
                WHERE("product_id= #{filterCriteria.productId}");
            }
            if(filter.getProgramId() != -1){  //Unless All programs selected
                WHERE("program_id = #{filterCriteria.programId}");
            }
            if(!filter.getAdjustmentTypeId().equals("-1") && filter.getAdjustmentTypeId().equals("0") ){
                WHERE("adjustment_type = #{filterCriteria.adjustmentTypeId}");
            }
        }
    }
}
