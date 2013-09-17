/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.apache.ibatis.jdbc.SqlBuilder;
import org.openlmis.report.model.filter.ConsumptionReportFilter;

import java.util.Map;
import org.apache.ibatis.type.JdbcType;
import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;
import java.util.Date;

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

        ConsumptionReportFilter filter  = (ConsumptionReportFilter)params.get("filterCriteria");
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
