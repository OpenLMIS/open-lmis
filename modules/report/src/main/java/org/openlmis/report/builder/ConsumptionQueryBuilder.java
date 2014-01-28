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

import org.apache.ibatis.jdbc.SqlBuilder;
import org.openlmis.report.model.params.ConsumptionReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class ConsumptionQueryBuilder {

    private static SqlBuilder SQL;

    public static String getQuery(Map params){
        return "select " +
                    "sum(quantitydispensed) consumption " +
                    ", product,productcategory category " +
                    ", ft.name facilityType " +
                    ", f.name facility " +
                "from " +
                    "requisition_line_items li " +
                    "join requisitions r on r.id = li.rnrid " +
                    "join facilities f on r.facilityid = f.id " +
                    "join facility_types ft on ft.id = f.typeid " +
                "group by " +
                    "li.product, li.productcategory, f.name, ft.name " +
                "order by " +
                    "li.productCategory, li.product";
    }

    public static String SelectFilteredSortedPagedConsumptionSql(Map params){

        ConsumptionReportParam filter  = (ConsumptionReportParam)params.get("filterCriteria");
        //ConsumptionReportSorter sorter = (ConsumptionReportSorter)params.get("SortCriteria");
        BEGIN();

        SELECT("sum(quantitydispensed) consumption,product, productcategory category, ft.name facilityType, f.name facility");
        FROM("requisition_line_items li");
        JOIN("requisitions r on r.id = li.rnrid");
        JOIN("facilities f on r.facilityid = f.id");
        JOIN("facility_types ft on ft.id = f.typeid");
        JOIN("processing_periods pp on pp.id = r.periodid");

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

        }
        GROUP_BY("li.product, li.productcategory, f.name, ft.name");
        ORDER_BY("li.productCategory, li.product");
        return SQL();
    }

}
