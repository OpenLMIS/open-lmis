/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

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
                        "    inner join program_products ON program_products.productid = products.id \n" +
                        "    inner join programs ON  program_products.programid = programs.id   AND  programs.id = r.programid       \n" +
                        "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityid         \n" +
                        "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid         \n" +
                        "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id         \n" +
                        "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.scheduleid         \n" +
                        "    inner join processing_periods ON processing_periods.scheduleid = processing_schedules.id  \n" +

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
        String facilityTypeId = (!params.containsKey("facilityTypeId") || params.get("facilityTypeId") == null) ? null :((String[])params.get("facilityTypeId"))[0];
        String facilityName = (!params.containsKey("facilityName") || params.get("facilityName") == null) ? null : ((String[])params.get("facilityName"))[0];
        String period =    (!params.containsKey("periodId") || params.get("periodId") == null) ? null : ((String[])params.get("periodId"))[0];
        String program =   (!params.containsKey("programId") || params.get("programId") == null) ? null : ((String[])params.get("programId"))[0];
        String product =   (!params.containsKey("productId") || params.get("productId") == null) ? null : ((String[])params.get("productId"))[0];
        String zone =     (!params.containsKey("zoneId") || params.get("zoneId") == null) ? null : ((String[])params.get("zoneId"))[0];
        String rgroup =     (!params.containsKey("rgroupId") || params.get("rgroupId") == null) ? null : ((String[])params.get("rgroupId"))[0];
        String schedule = (!params.containsKey("facilityTypeId") || params.get("scheduleId") == null) ? null : ((String[])params.get("scheduleId"))[0];

        if (period != null &&  !period.equals("undefined") && !period.isEmpty() && !period.equals("0")  && !period.equals("-1")){
            predicate += " and r.periodid = "+ period;
        }
        if (program != null &&  !program.equals("undefined") && !program.isEmpty() && !program.equals("0")  && !program.equals("-1")) {

            predicate += " and r.programid = "+ program;
        }
        if (zone != null &&  !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {

            predicate += " and facilities.geographiczoneid = "+ zone;
        }
        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and program_products.productid = "+ product;
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

        return predicate;
    }

    public static String getTotalCount(Map params){
        String query = "select count(*) \n"+
                "    from facilities        \n" +
                    "    inner join requisitions r ON  r.facilityid = facilities.id         \n" +
                    "    inner join requisition_line_items li ON li.rnrid = r.id         \n" +
                    "    inner join products ON products.code  ::text =   li.productcode  ::text       \n" +
                    "    inner join program_products ON program_products.productid = products.id \n" +
                    "    inner join programs ON  program_products.programid = programs.id   AND  programs.id = r.programid       \n" +
                    "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityid         \n" +
                    "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid         \n" +
                    "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id         \n" +
                    "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.scheduleid         \n" +
                    "    inner join processing_periods ON processing_periods.scheduleid = processing_schedules.id  \n" +

                writePredicates(params);

        return query;
    }




}
