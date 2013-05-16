package org.openlmis.report.builder;

import org.openlmis.report.model.filter.AdjustmentSummaryReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

/**
 * User: Wolde
 * Date: 5/10/13
 * Time: 3:20 PM
 */
public class AdjustmentSummaryQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){

        AdjustmentSummaryReportFilter filter  = (AdjustmentSummaryReportFilter)params.get("filterCriteria");
        Map<String, String[]> sorter = ( Map<String, String[]>)params.get("SortCriteria");
        BEGIN();

        SELECT("product , MAX(productcategory) category, MAX(ft.name) facilityType, MAX(f.name) facilityName, at.name adjustmentType, MAX(li.totallossesandadjustments) adjustment, MAX(s.name) supplyingFacility");
        FROM("requisition_line_item_losses_adjustments la");
        JOIN("requisition_line_items li on li.id = la.requisitionlineitemid");
        JOIN("losses_adjustments_types at on at.name = la.type");
        JOIN("requisitions r on r.id = li.rnrid");
        JOIN("facilities f on r.facilityid = f.id");
        JOIN("facility_types ft on ft.id = f.typeid");
        JOIN("processing_periods pp on pp.id = r.periodid");
        JOIN("products pr on pr.code = li.productcode");
        JOIN("product_categories prc on prc.id = pr.categoryid");
        JOIN("requisition_group_members rgm on rgm.facilityid = f.id");
        JOIN("supply_lines sl on sl.supervisorynodeid = r.supervisorynodeid and r.programid = sl.programid");
        JOIN("facilities s on s.id = sl.supplyingfacilityid and f.id = s.id");

        if(filter != null){
            if (filter.getFacilityTypeId() != 0) {
                WHERE("ft.id = #{filterCriteria.facilityTypeId}");
            }
//            if (filter.getZoneId() != 0) {
//                WHERE("f.geographiczoneid = #{filterCriteria.zoneId}");
//            }
            if (filter.getStartDate() != null) {
                WHERE("pp.startDate >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("pp.endDate <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 ){
                WHERE("prc.id = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0){
                WHERE("rgm.id = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != 0){
                WHERE("pr.id = #{filterCriteria.productId}");
            }
            if(filter.getProgramId() != 0){
                WHERE("r.programid = #{filterCriteria.programId}");
            }
            if(filter.getAdjustmentTypeId() != 0){
                WHERE("la.type = #{filterCriteria.adjustmentTypeId}");
            }
        }
        GROUP_BY("product , at.name, li.totallossesandadjustments");
         return SQL();
    }

    public static String SelectFilteredSortedPagedRecordsCount(Map params){

        AdjustmentSummaryReportFilter filter  = (AdjustmentSummaryReportFilter)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) perCounts");
        FROM("requisition_line_item_losses_adjustments la");
        JOIN("requisition_line_items li on li.id = la.requisitionlineitemid");
        JOIN("losses_adjustments_types at on at.name = la.type");
        JOIN("requisitions r on r.id = li.rnrid");
        JOIN("facilities f on r.facilityid = f.id");
        JOIN("facility_types ft on ft.id = f.typeid");
        JOIN("processing_periods pp on pp.id = r.periodid");
        JOIN("products pr on pr.code = li.productcode");
        JOIN("product_categories prc on prc.id = pr.categoryid");
        JOIN("requisition_group_members rgm on rgm.facilityid = f.id");
        JOIN("supply_lines sl on sl.supervisorynodeid = r.supervisorynodeid and r.programid = sl.programid");
        JOIN("facilities s on s.id = sl.supplyingfacilityid and f.id = s.id");


        if(filter != null){
            if (filter.getFacilityTypeId() != 0) {
                WHERE("ft.id = #{filterCriteria.facilityTypeId}");
            }
            if (filter.getZoneId() != 0) {
                WHERE("f.geographiczoneid = #{filterCriteria.zoneId}");
            }
            if (filter.getStartDate() != null) {
                WHERE("pp.startDate >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("pp.endDate <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 ){
                WHERE("prc.id = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0){
                WHERE("rgm.id = #{filterCriteria.rgroupId}");
            }
            if(filter.getProgramId() != 0){
                WHERE("r.programid = #{filterCriteria.programId}");
            }
            if(filter.getAdjustmentTypeId() != 0){
                WHERE("la.type = #{filterCriteria.adjustmentTypeId}");
            }

        }
        GROUP_BY("product , at.name, li.totallossesandadjustments");
        String subQuery = SQL().toString();

        BEGIN();
        SELECT("COUNT(*)");
        FROM("( "+ subQuery +" ) as counts");
        return SQL();
    }
}
