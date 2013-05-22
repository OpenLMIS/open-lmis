package org.openlmis.report.builder;

import org.openlmis.report.model.filter.AverageConsumptionReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class AverageConsumptionQueryBuilder {

    public static String getQuery(Map params){
        return "select p.code || ' ' || p.manufacturer as productDescription, pp.dosespermonth average " +
                "from products p " +
                "inner join program_products pp on p.id = pp.productId ";
    }

    public static String SelectFilteredSortedPagedAverageConsumptionSql(Map params){

        AverageConsumptionReportFilter filter  = (AverageConsumptionReportFilter)params.get("filterCriteria");
        Map<String, String[]> sorter = ( Map<String, String[]>)params.get("SortCriteria");
        BEGIN();

        SELECT("coalesce( avg(quantitydispensed),0) average, product, productcode, productcategory category, ft.name facilityType, f.name facilityName,  MAX(s.name) supplyingFacility, MAX(li.maxmonthsofstock) MaxMOS, MAX(li.maxmonthsofstock) minMOS");
        FROM("requisition_line_items li");
        JOIN("requisitions r on r.id = li.rnrid");
        JOIN("facilities f on r.facilityid = f.id");
        JOIN("facility_types ft on ft.id = f.typeid");
        JOIN("processing_periods pp on pp.id = r.periodid");
        JOIN("products pr on pr.code = li.productcode");
        JOIN("product_categories prc on prc.id = pr.categoryid");
        JOIN("requisition_group_members rgm on rgm.facilityid = f.id");
        LEFT_OUTER_JOIN("supply_lines sl on sl.supervisorynodeid = r.supervisorynodeid and r.programid = sl.programid");
        LEFT_OUTER_JOIN("facilities s on s.id = sl.supplyingfacilityid");

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
            if(filter.getProductId() != 0){
                WHERE("pr.id = #{filterCriteria.productId}");
            }
            if(filter.getProgramId() != 0){
                WHERE("r.programid = #{filterCriteria.programId}");
            }
        }
        GROUP_BY("li.product, li.productcategory,  f.name, ft.name, li.productcode");
        ORDER_BY( QueryHelpers.getSortOrder(params, "ft.name, f.name , li.productcategory, li.product, li.productcode"));
        return SQL();
    }



    public static String SelectFilteredSortedPagedAverageConsumptionCountSql(Map params){

        AverageConsumptionReportFilter filter  = (AverageConsumptionReportFilter)params.get("filterCriteria");
        //ConsumptionReportSorter sorter = (ConsumptionReportSorter)params.get("SortCriteria");

        BEGIN();
        SELECT("COUNT(*) perCounts");
        FROM("requisition_line_items li");
        JOIN("requisitions r on r.id = li.rnrid");
        JOIN("facilities f on r.facilityid = f.id");
        JOIN("facility_types ft on ft.id = f.typeid");
        JOIN("processing_periods pp on pp.id = r.periodid");
        JOIN("products pr on pr.code = li.productcode");
        JOIN("product_categories prc on prc.id = pr.categoryid");
        JOIN("requisition_group_members rgm on rgm.facilityid = f.id");
        LEFT_OUTER_JOIN("supply_lines sl on sl.supervisorynodeid = r.supervisorynodeid and r.programid = sl.programid");
        LEFT_OUTER_JOIN("facilities s on s.id = sl.supplyingfacilityid");
       // JOIN("programs p on p.id = r.programid");

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

        }
        GROUP_BY("li.product, li.productcategory, f.name, ft.name, li.productcode");
        String subQuery = SQL().toString();

        BEGIN();
        SELECT("COUNT(*)");
        FROM("( "+ subQuery +" ) as counts");
        return SQL();
    }

}
