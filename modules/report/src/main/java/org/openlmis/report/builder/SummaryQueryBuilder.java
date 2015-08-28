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

import org.openlmis.report.util.StringHelper;

import java.util.Map;

public class SummaryQueryBuilder {

  private static String getAggregateSelect(){
    return "select " +
      " li.productCode as code" +
      ", li.product" +
      ", li.productCategory as category" +
      ", li.dispensingUnit as unit" +
      ", sum(li.beginningBalance) as openingBalance" +
      ", sum(li.quantityReceived) as receipts" +
      ", sum(li.quantityDispensed) as issues" +
      ", sum(li.totalLossesAndAdjustments) as adjustments" +
      ", sum(li.stockInHand) as closingBalance " +
      ", sum(li.quantityApproved) as reorderAmount " +
      ", sum(0) as stockOutRate " +
      "    from facilities   " +
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
      "    inner join processing_periods ON processing_periods.id = r.periodId  ";
  }

  private static String getAggregateGroupBy(){
    return  " group by li.productCode, li.productCategory, li.product, li.dispensingUnit  " +
            " order by productCategory asc, product asc";
  }

  private static String getDisaggregatedSelect(){
    return "select " +
      " li.productCode as code " +
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
      ", (li.stockInHand) as closingBalance " +
      "    from facilities   " +
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
      "    inner join processing_periods ON processing_periods.id = r.periodId  ";
  }

  private static String getDisAggregatedGroupBy(){
    return " order by productCategory asc, product asc, facility asc";
  }

  public static String getQuery(Map params) {
    if (params.containsKey("param1")) {
      params = (Map) params.get("param1");
    }
    Boolean disaggregated = StringHelper.isBlank(params, "disaggregated")? false: Boolean.parseBoolean(StringHelper.getValue(params, "disaggregated"));
    return (disaggregated)?  getDisaggregatedSelect() + getPredicates(params) + getDisAggregatedGroupBy() : getAggregateSelect() + getPredicates(params) + getAggregateGroupBy();
  }

  private static String getPredicates(Map params) {
    String predicate = " WHERE r.status in ('APPROVED','RELEASED') ";
    String facilityTypeId = StringHelper.isBlank(params, "facilityType") ? null : ((String[]) params.get("facilityType"))[0];
    String facilityName = StringHelper.isBlank(params, "facilityName") ? null : ((String[]) params.get("facilityName"))[0];
    String period = StringHelper.isBlank(params, "period") ? null : ((String[]) params.get("period"))[0];
    String productCategory = StringHelper.getValue(params, "productCategory");
    String program = StringHelper.isBlank(params, "program") ? null : ((String[]) params.get("program"))[0];
    String product = StringHelper.isBlank(params, "product") ? null : ((String[]) params.get("product"))[0];
    String zone = StringHelper.isBlank(params, "zone") ? null : ((String[]) params.get("zone"))[0];
    String schedule = StringHelper.isBlank(params, "schedule") ? null : ((String[]) params.get("schedule"))[0];
    String facilityId = StringHelper.isBlank(params, "facility") ? null : ((String[]) params.get("facility"))[0];


    predicate += " and r.periodId = " + period;
    predicate += " and r.programId = " + program;

    if(productCategory != null && !productCategory.equals("undefined") && !productCategory.isEmpty() && !productCategory.equals("0") && !productCategory.equals("-1")){
      predicate += "and pps.productCategoryId = " + productCategory;
    }

    if (product != null && !product.equals("undefined") && !product.isEmpty() && !product.equals("0") && !product.equals("-1")) {
      predicate += " and products.id = " + product;
    }

    if (schedule != null && !schedule.equals("undefined") && !schedule.isEmpty() && !schedule.equals("0") && !schedule.equals("-1")) {
      predicate += " and processing_schedules.id = " + schedule;
    }

    if (zone != null && !zone.equals("0") && !zone.isEmpty() && !zone.endsWith("undefined")) {
      predicate += (" and (gz.district_id = " + zone + " or gz.zone_id = " + zone + " or gz.region_id = " + zone + " or gz.parent = " + zone + " )");
    }

    if (facilityTypeId != null && !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") && !facilityTypeId.equals("-1")) {
      predicate += " and facilities.typeid = " + facilityTypeId;
    }

    if (facilityName != null && !facilityName.equals("undefined") && !facilityName.isEmpty()) {
      predicate += " and facilities.name = '" + facilityName + "'";
    }

    if (facilityId != null && !facilityId.equals("") && !facilityId.equals("undefined") && !facilityId.equals("0")) {
      predicate += " and facilities.id = " + facilityId + "";
    }
    return predicate;
  }

}
