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

import org.openlmis.report.util.StringHelper;

import java.util.Map;

public class SummaryQueryBuilder {

    public static String getQuery(Map params){

        String query = "select " +
                    " li.productcode as code" +
                    ", li.product" +
                    ", li.productcategory as category" +
                    ", li.dispensingunit as unit" +
                    ", sum(li.beginningBalance) as openingBalance" +
                    ", sum(li.quantityReceived) as quantityReceived" +
                    ", sum(li.quantitydispensed) as actualDispensedQuantity" +
                    ", sum(li.quantitydispensed) as adjustedDispensedQuantity" +
                    ", sum(li.quantitydispensed) as adjustedDistributedQuantity" +
                    ", sum(li.stockInHand) as balanceOnHand " +
                    ", sum(0) as stockOutRate " +
                    ", sum(1.0) / (select count(*) from facilities) as productReportingRate " +

                    "    from facilities        \n" +
                        "    inner join requisitions r ON  r.facilityid = facilities.id         \n" +
                        "    inner join requisition_line_items li ON li.rnrid = r.id         \n" +
                        "    inner join products ON products.code  ::text =   li.productcode  ::text       \n" +
                        "   -- inner join program_products ON program_products.productid = products.id \n" +
                        "    inner join programs ON  r.programid = programs.id     \n" +
                        "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityid         \n" +
                        "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid         \n" +
                        "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   " +
                                        " AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id         \n" +
                        "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.scheduleid         \n" +
                        "    inner join processing_periods ON processing_periods.id = r.periodid \n" +

                writePredicates(params)+

                " group by li.productcode, li.productcategory, li.product, li.dispensingunit" +
                " order by " + QueryHelpers.getSortOrder(params, "productcategory asc, product asc");
            return query;
    }

    private static String writePredicates(Map params){
        String predicate = " WHERE r.status in ('APPROVED','RELEASED') ";

        // if for some reason the map is coming as a map of maps, decode it here
        if(params.containsKey("param1")){
          params = (Map) params.get("param1");
        }
        String facilityTypeId =  StringHelper.isBlank( params,"facilityType")? null :((String[])params.get("facilityType"))[0];
        String facilityName = StringHelper.isBlank( params, "facilityName") ? null : ((String[])params.get("facilityName"))[0];
        String period =    StringHelper.isBlank( params, "period") ? null : ((String[])params.get("period"))[0];
        String program =   StringHelper.isBlank( params,"program") ? null : ((String[])params.get("program"))[0];
        String product =   StringHelper.isBlank( params,"product") ? null : ((String[])params.get("product"))[0];
        String zone =     StringHelper.isBlank( params,"zone") ? null : ((String[])params.get("zone"))[0];
        String rgroup =     StringHelper.isBlank( params,"requisitionGroup") ? null : ((String[])params.get("requisitionGroup"))[0];
        String schedule = StringHelper.isBlank( params,"schedule")  ? null : ((String[])params.get("schedule"))[0];
        String facilityId = StringHelper.isBlank( params, "facility")  ? null : ((String[])params.get("facility"))[0];


        predicate += " and r.periodid = " + period;
        predicate += " and r.programid = " + program;

        if (zone != null &&  !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {
            predicate += " and facilities.geographiczoneid = "+ zone;
        }

        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and products.id = "+ product;
        }
        if (schedule != null &&  !schedule.equals("undefined") && !schedule.isEmpty() && !schedule.equals("0") &&  !schedule.equals("-1")) {

            predicate += " and processing_schedules.id = "+ schedule;
        }
        if (rgroup != null &&  !rgroup.equals("undefined") && !rgroup.isEmpty() && !rgroup.equals("0") &&  !rgroup.equals("-1")) {

            predicate += " and requisition_groups.id = "+ rgroup;
        }
        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {

            predicate += " and facilities.typeid = "+ facilityTypeId;
        }
        if (facilityName != null &&  !facilityName.equals("undefined") && !facilityName.isEmpty() ) {

            predicate += " and facilities.name = '"+ facilityName +"'";
        }
        if(facilityId != null && !facilityId.equals("") && !facilityId.equals( "undefined") && !facilityId.equals("0")){
            predicate += " and facilities.id = "+ facilityId +"";
        }
        return predicate;
    }

}
