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

import org.openlmis.report.model.params.SummaryReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class SummaryQueryBuilder {

  private static String getAggregateSelect(SummaryReportParam param){
    BEGIN();
    SELECT( " li.productCode as code" +
      ", li.product" +
      ", li.productCategory as category" +
      ", li.dispensingUnit as unit" +
      ", sum(li.beginningBalance) as openingBalance" +
      ", sum(li.quantityReceived) as receipts" +
      ", sum(li.quantityDispensed) as issues" +
      ", sum(li.totalLossesAndAdjustments) as adjustments" +
      ", sum(li.stockInHand) as closingBalance " +
      ", sum(li.quantityApproved) as reorderAmount " +
      ", sum(0) as stockOutRate " );
    FROM(" facilities   " +
      "    inner join requisitions r ON  r.facilityId = facilities.id   " +
      "    inner join requisition_line_items li ON li.rnrId = r.id    " +
      "    inner join products ON products.code  ::text =   li.productCode  ::text      " +
      "    inner join vw_districts gz on gz.district_id = facilities.geographicZoneId " +
      "    inner join programs ON  r.programId = programs.id " +
      "    inner join program_products pps ON  r.programId = pps.programId and products.id = pps.productId " +
      "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityId " +
      "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitionGroupId " +
      "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programId = programs.id   " +
      "               AND requisition_group_program_schedules.requisitionGroupId = requisition_groups.id " +
      "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.scheduleId  " +
      "    inner join processing_periods ON processing_periods.id = r.periodId  ");
    writePredicates(param);
    GROUP_BY("li.productCode, li.productCategory, li.product, li.dispensingUnit");
    ORDER_BY("productCategory asc, product asc");
    return SQL();
  }

  private static String getDisaggregatedSelect(SummaryReportParam param){
    BEGIN();
    SELECT( " li.productCode as code " +
      ", li.product" +
      ", facilities.code as facilityCode" +
      ", facilities.name as facility" +
      ", facility_types.name as facilityType" +
      ", li.productCategory as category" +
      ", li.dispensingUnit as unit" +
      ", (li.beginningBalance) as openingBalance" +
      ", (li.quantityReceived) as receipts" +
      ", (li.quantityDispensed) as issues" +
      ", (li.quantityApproved) as reorderAmount " +
      ", (li.totalLossesAndAdjustments) as adjustments" +
      ", (li.stockInHand) as closingBalance " );

    FROM(" facilities   " +
      " inner join facility_types on facility_types.id = facilities.typeId " +
      "    inner join requisitions r ON  r.facilityId = facilities.id   " +
      "    inner join requisition_line_items li ON li.rnrId = r.id    " +
      "    inner join products ON products.code  ::text =   li.productCode  ::text      " +
      "    inner join vw_districts gz on gz.district_id = facilities.geographicZoneId " +
      "    inner join programs ON  r.programId = programs.id " +
      "    inner join program_products pps ON  r.programId = pps.programId and products.id = pps.productId " +
      "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityId " +
      "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitionGroupId " +
      "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programId = programs.id   " +
      "               AND requisition_group_program_schedules.requisitionGroupId = requisition_groups.id " +
      "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.scheduleId  " +
      "    inner join processing_periods ON processing_periods.id = r.periodId  ");
    writePredicates(param);
    ORDER_BY("productCategory asc, product asc, facility asc");
    return SQL();
  }


  public static String getQuery(Map params) {
    SummaryReportParam filter = (SummaryReportParam) params.get("filterCriteria");
    if(filter.getDisaggregated()){
      return getDisaggregatedSelect(filter);
    }
    return getAggregateSelect(filter) ;
  }

  private static void writePredicates(SummaryReportParam filter) {

    WHERE(rnrStatusFilteredBy("r.status", "'APPROVED', 'RELEASED'"));
    WHERE(periodIsFilteredBy("r.periodId"));
    WHERE(programIsFilteredBy("r.programId"));

    if(filter.getProductCategory() > 0){
      WHERE(productCategoryIsFilteredBy("pps.productCategoryId"));
    }

    if(filter.getProduct() > 0){
      productFilteredBy("products.id");
    }

    if(filter.getZone() > 0){
      WHERE(geoZoneIsFilteredBy("gz"));
    }

    if (filter.getFacilityType() > 0) {
      WHERE(facilityTypeIsFilteredBy("facilities.typeId"));
    }

    if (filter.getFacility() > 0) {
      WHERE(facilityIsFilteredBy("facilities.id"));
    }
  }

}
