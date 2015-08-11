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
