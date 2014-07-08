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

import org.openlmis.report.model.params.DistrictConsumptionReportParam;

import java.util.Map;

public class DistrictConsumptionQueryBuilder {

  public static String SelectFilteredSortedPagedRecords(Map params){

     DistrictConsumptionReportParam filter  = (DistrictConsumptionReportParam)params.get("filterCriteria");

     String query = "WITH temp AS (select product,zone_name, SUM(normalizedconsumption) normalizedconsumption "+

    "from vw_district_consumption_summary "+
     writePredicates(filter)+
    "group by product,zone_name "+
    "order by product) "+

    "select t.product, t.zone_name district, t.normalizedconsumption consumption, case when temp2.total > 0 THEN round(((t.normalizedconsumption*100)/temp2.total),1) ELSE temp2.total END totalPercentage  "+
    "from temp t "+
    "INNER JOIN ( select product,SUM(normalizedconsumption) total "+
    "from temp "+
    "group by product "+
    "order by product) temp2 ON t.product = temp2.product ";


    return query;
}

  private static String writePredicates(DistrictConsumptionReportParam filter){
        String predicate = "";
        if(filter != null){
          predicate = "where processing_periods_id = " + filter.getPeriod() + " ";
            if (filter.getZoneId() != 0) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " zone_id = #{filterCriteria.zoneId}";
            }

            if(filter.getProductCategoryId() != 0 ){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " product_category_id = #{filterCriteria.productCategoryId}";
            }

            if(filter.getProductId() != 0){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " product_id= #{filterCriteria.productId}";
            }
            if(filter.getProgramId() != 0){
                predicate = predicate.isEmpty() ?" where " : predicate +  " and ";
                predicate = predicate + " program_id = #{filterCriteria.programId}";
            }


        }

        return predicate;
    }

  public static String GetAggregateConsumptionReport(Map params){
    DistrictConsumptionReportParam filter   = (DistrictConsumptionReportParam)params.get("filterCriteria");
    Long userId                             = (Long) params.get("userId");

    String predicates = "";


    if(filter.getProductId() > 0 ){
        predicates = predicates + " and p.id = " + filter.getProductId();
    }

    if(filter.getProductCategoryId() > 0){
      predicates = predicates + " and ppc.productCategoryId = " + filter.getProductCategoryId();
    }

    if(filter.getZoneId() != 0){
      predicates = predicates + " and ( d.zone_id = " + filter.getZoneId() +" or d.parent = " +filter.getZoneId() + " or d.region_id = " + filter.getZoneId() + " or d.district_id = " + filter.getZoneId() + ") " ;
    }

    String query = "SELECT li.productCode code, li.product, sum(li.quantityDispensed) dispensed, sum(li.normalizedConsumption) consumption FROM requisition_line_items li \n" +
              " JOIN requisitions r on r.id = li.rnrid " +

              " JOIN facilities f on r.facilityId = f.id " +
              " JOIN vw_districts d on d.district_id = f.geographicZoneId " +

              " JOIN requisition_group_members rgm on rgm.facilityId = r.facilityId\n" +
              " JOIN programs_supported ps  on ps.programId = r.programId and r.facilityId = ps.facilityId\n" +
              " JOIN processing_periods pp on pp.id = r.periodId " +
              " JOIN products p on p.code::text = li.productCode::text " +
              " JOIN program_products ppc on ppc.programId = r.programId and ppc.productId = p.id " +
              " JOIN requisition_group_program_schedules rgps on rgps.requisitionGroupID = rgm.requisitionGroupId and pp.scheduleId = rgps.scheduleId\n" +
              " WHERE " +
              "   f.id in (select facility_id from vw_user_facilities where user_id = " + userId + " and program_id = "  + filter.getProgramId() + ") " +
              "   and r.periodId = " + filter.getPeriod() + " and r.programId =  " + filter.getProgramId() + predicates +
              " GROUP BY li.productCode, li.product" +
              " ORDER BY li.product ";

    return query;
  }
}
