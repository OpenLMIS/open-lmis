package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:40 PM
 */
public class StockImbalanceQueryBuilder {
    public static String getQuery(Map params){

        String query = "SELECT distinct\n" +
                " facilities.name AS Facility,\n" +
                "facilities.name AS SupplyingFacility,\n" +
                " requisition_line_items.product AS Product,\n" +
                " requisition_line_items.stockinhand physicalCount,\n" +
                " requisition_line_items.amc,\n" +
                " CASE WHEN coalesce(requisition_line_items.AMC,0) = 0 THEN 0 ELSE round(requisition_line_items.stockinhand/requisition_line_items.AMC,1) END AS months,\n" +
                " coalesce(CASE WHEN (requisition_line_items.stockinhand - coalesce(amc,0) * fn_get_max_mos (programs.id, facilities.id, products.code)) < 0 THEN 0 ELSE (requisition_line_items.stockinhand - coalesce(amc,0) * fn_get_max_mos (programs.id, facilities.id, products.code)) END ,0) AS orderQuantity,\n" +
                " CASE WHEN requisition_line_items.stockinhand = 0 THEN 'Stockedout' ELSE\n" +
                "  CASE WHEN requisition_line_items.stockinhand > 0 AND requisition_line_items.stockinhand  < coalesce(amc,0) * fn_get_max_mos (programs.id, facilities.id, products.code) THEN 'Understocked' ELSE\n" +
                "\t\tCASE WHEN requisition_line_items.stockinhand  > coalesce(amc,0) * fn_get_max_mos (programs.id, facilities.id, products.code) THEN 'Understocked' ELSE '' END\n" +
                "  END\n" +
                " END AS Status\n" +
                "\n" +
                "\n" +
                "FROM\n" +
                " facilities\n" +
                "INNER JOIN  facility_types ON  facilities.typeid =  facility_types.id\n" +
                "INNER JOIN  requisitions ON  requisitions.facilityid =  facilities.id\n" +
                "INNER JOIN  requisition_line_items ON  requisition_line_items.rnrid =  requisitions.id\n" +
                "INNER JOIN  products ON  products.code =  requisition_line_items.productcode\n" +
                "INNER JOIN  program_products ON  program_products.productid =  products.id\n" +
                "INNER JOIN  programs ON  program_products.programid =  programs.id AND  programs.id =  requisitions.programid\n" +
                "INNER JOIN  programs_supported ON  programs.id =  programs_supported.programid AND  facilities.id =  programs_supported.facilityid\n" +
                "INNER JOIN  requisition_group_members ON  facilities.id =  requisition_group_members.facilityid\n" +
                "INNER JOIN  requisition_groups ON  requisition_groups.id =  requisition_group_members.requisitiongroupid\n" +
                "INNER JOIN  requisition_group_program_schedules ON  requisition_group_program_schedules.programid =  programs.id AND  requisition_group_program_schedules.requisitiongroupid =  requisition_groups.id\n" +
                "INNER JOIN  processing_schedules ON  processing_schedules.id =  requisition_group_program_schedules.programid\n" +
                "INNER JOIN  processing_periods ON  processing_periods.scheduleid =  processing_schedules.id\n" +
                "where requisition_line_items.stockinhand is not null" +"\n";//+
               // writePredicates(params)+ "\n";

              //  "group by facilities.name,li.productcode, li.product, li.productcategory ,requisition_groups.id \n" +
               // " order by " + QueryHelpers.getSortOrder(params, "facilities.name asc,li.productcode asc,  li.product asc, li.productcategory asc , requisition_groups.id asc");
        return query;
    }
    private static String writePredicates(Map params){
        String predicate = "WHERE r.status = 'RELEASED' ";
        String facilityTypeId =  params.get("facilityTypeId") == null ? null :((String[])params.get("facilityTypeId"))[0];
        String facilityName = params.get("facilityName") == null ? null : ((String[])params.get("facilityName"))[0];
        String period =    params.get("periodId") == null ? null : ((String[])params.get("periodId"))[0];
        String program =   params.get("programId") == null ? null : ((String[])params.get("programId"))[0];
        String product =   params.get("productId") == null ? null : ((String[])params.get("productId"))[0];
        String zone =     params.get("zoneId") == null ? null : ((String[])params.get("zoneId"))[0];
        String rgroup =     params.get("rgroupId") == null ? null : ((String[])params.get("rgroupId"))[0];
        String schedule = params.get("scheduleId") == null ? null : ((String[])params.get("scheduleId"))[0];

        predicate += " and processing_periods.id = "+ period;

        predicate += " and programs.id = "+ program;

        predicate += " and processing_schedules.id = "+ schedule;

        if (zone != null &&  !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {

            predicate += " and facilities.geographiczoneid = "+ zone;
        }
        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and program_products.productid = "+ product;
        }

        if (rgroup != null &&  !rgroup.equals("undefined") && !rgroup.isEmpty() && !rgroup.equals("0") &&  !rgroup.equals("-1")) {

            predicate += " and requisition_groups.id = "+ rgroup;
        }
        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {

            predicate += " and facility_types.id = "+ facilityTypeId;
        }
        if (facilityName != null &&  !facilityName.equals("undefined") && !facilityName.isEmpty() ) {

            predicate += " and facilities.name = '"+ facilityName +"'";
        }

        return predicate;
    }
}
