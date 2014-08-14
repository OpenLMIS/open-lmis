
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

import org.openlmis.report.model.params.AverageConsumptionReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class AverageConsumptionQueryBuilder {

    public static String getQuery(Map params){
        return "select p.code || ' ' || p.manufacturer as productDescription, pp.dosespermonth average " +
                "from products p " +
                "inner join program_products pp on p.id = pp.productId ";
    }

    public static String SelectFilteredSortedPagedAverageConsumptionSql(Map params){

        AverageConsumptionReportParam filter  = (AverageConsumptionReportParam)params.get("filterCriteria");
        Map<String, String[]> sorter = ( Map<String, String[]>)params.get("SortCriteria");
        BEGIN();

        SELECT("coalesce( avg(quantitydispensed),0) average, product, productcode, productcategory category, ft.name facilityType, f.name facilityName,MAX(ps.name) schedulename, MAX(pp.name) periodname,  MAX(s.name) supplyingFacility, MAX(ft.nominalmaxmonth) MaxMOS, MAX(ft.nominaleop) minMOS");
        FROM("requisition_line_items li");
        JOIN("requisitions r on r.id = li.rnrid");
        JOIN("facilities f on r.facilityid = f.id");
        JOIN("facility_types ft on ft.id = f.typeid");
        JOIN("processing_periods pp on pp.id = r.periodid");
        JOIN("processing_schedules ps on pp.scheduleid = ps.id");
        JOIN("products pr on pr.code = li.productcode");
        JOIN("program_products prp on prp.productid = pr.id");
        JOIN("product_categories prc on prc.id = prp.productcategoryid");
        JOIN("requisition_group_members rgm on rgm.facilityid = f.id");
        LEFT_OUTER_JOIN("supply_lines sl on sl.supervisorynodeid = r.supervisorynodeid and r.programid = sl.programid");
        LEFT_OUTER_JOIN("facilities s on s.id = sl.supplyingfacilityid");

        WHERE("r.status in ('APPROVED', 'RELEASED')");
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

            if(!filter.getProductId().equals("{0}") && !filter.getProductId().equals("{}")){
                WHERE("pr.id = ANY( #{filterCriteria.productId}::INT[] ) ");
            }
            if(filter.getProgramId() != 0){
                WHERE("r.programid = #{filterCriteria.programId}");
            }
        }
        GROUP_BY("li.product, li.productcategory,  f.name, ft.name, li.productcode");
        ORDER_BY( QueryHelpers.getSortOrder(params, "ft.name, f.name , li.productcategory, li.product, li.productcode"));

        // copy the sql over to a variable, this makes the debugging much more possible.
        String sql = SQL();
        return sql;
    }

}
