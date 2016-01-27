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


import org.openlmis.report.model.params.SeasonalRationingReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class SeasonalRationingQueryBuilder {

    public static String SelectFilteredSortedPagedRecords(Map params){

        SeasonalRationingReportParam filter  = (SeasonalRationingReportParam)params.get("filterCriteria");

        BEGIN();
        SELECT(" facilities.name facilityname, gz.district_name as district, products.primaryname || ' (' || products.code || ')' as productname, adjustmentTypes.name as adjustmenttype, " +
                " adjustmentfactors.name as adjustmentbasis, adjustment.startdate, adjustment.enddate, adjustment.minmonthsofstock, adjustment.maxmonthsofstock, adjustment.formula");
        FROM(" order_quantity_adjustment_products adjustment");
        JOIN("facilities ON adjustment.facilityid = facilities.id ");
        JOIN("products ON products.id = adjustment.productid ");
        JOIN("order_quantity_adjustment_types adjustmentTypes ON adjustmentTypes.id = adjustment.typeid ");
        JOIN("order_quantity_adjustment_factors adjustmentfactors ON  adjustmentfactors.id = adjustment.factorid");
        JOIN("program_products ON program_products.productid = products.id");
        JOIN("product_categories ON program_products.productcategoryid = product_categories.id");
        JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
        writePredicates(filter);
        ORDER_BY("gz.district_name asc, facilities.name asc, products.primaryname asc, adjustmentfactors.name asc");
        return SQL();
    }

    private static void writePredicates(SeasonalRationingReportParam filter ){

        WHERE("adjustment.facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");

        if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
            WHERE("product_categories.id = #{filterCriteria.productCategoryId}");
        }

        if(filter.getProductId() == -1){
            WHERE("products.tracer = true");
          }
        else if(filter.getProductId() != 0){
            WHERE("adjustment.productid = " + filter.getProductId());
        }

        if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
            WHERE("(gz.district_id = #{filterCriteria.zoneId} or gz.zone_id = #{filterCriteria.zoneId} or gz.region_id = #{filterCriteria.zoneId} or gz.parent = #{filterCriteria.zoneId})");
        }
    }
}
