package org.openlmis.report.builder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SelectBuilder.*;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class SummaryQueryBuilder {

    public static String getQuery(Map params){

        String query = "  select li.productcode code,li.productcategory as category, li.product,SUM(li.beginningBalance) openingBalance, SUM(li.quantityreceived) receipts, SUM(li.quantitydispensed) issues, SUM(li.totallossesandadjustments) adjustments,  \n" +
                "    (((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments)) closingBalance, SUM(li.maxmonthsofstock) monthsOfStock, SUM(li.amc) averageMonthlyConsumption       \n" +
                "    ,(SUM(li.amc) * SUM(facility_approved_products.maxmonthsofstock)) maximumStock,\n" +
                "\t   case when(SUM(li.amc) * SUM(facility_approved_products.maxmonthsofstock)) - (((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments)) > 0 then \n" +
                "   (SUM(li.amc) * SUM(facility_approved_products.maxmonthsofstock)) - (((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments)) ELSE 0 end\treorderAmount       \n" +
                "    from facilities        \n" +
                "    inner join facility_types ON facilities.typeid = facility_types.id       \n" +
                "    inner join geographic_zones on geographic_zones.id = facilities.geographiczoneid       \n" +
                "    inner join requisitions r ON  r.facilityid = facilities.id         \n" +
                "    inner join requisition_line_items li ON li.rnrid = r.id         \n" +
                "    inner join products ON products.code  ::text =   li.productcode  ::text       \n" +
                "    inner join program_products ON program_products.productid = products.id \n" +
                "\tinner join facility_approved_products ON facility_approved_products.facilitytypeid = facility_types.id AND facility_approved_products.programproductid = program_products.id\n" +
                "    inner join programs ON  program_products.programid = programs.id   AND  programs.id = r.programid       \n" +
                "    inner join programs_supported ON  programs.id = programs_supported.programid   AND   facilities.id = programs_supported.facilityid         \n" +
                "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityid         \n" +
                "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid         \n" +
                "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id         \n" +
                "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.programid         \n" +
                "    inner join processing_periods ON processing_periods.scheduleid = processing_schedules.id " +

                writePredicates(params)+

                "group by li.productcode, li.product, li.productcategory ,requisition_groups.id" +
                " order by " + QueryHelpers.getSortOrder(params, "li.productcode asc, li.product asc, li.productcategory asc , requisition_groups.id asc");
            return query;
    }
    private static String writePredicates(Map params){
        String predicate = "WHERE r.status = 'RELEASED' ";
        String facilityTypeId =  ((String[])params.get("facilityTypeId"))[0];
        String facilityName =  ((String[])params.get("facilityName"))[0];
        String period =    ((String[])params.get("periodId"))[0];
        String program =   ((String[])params.get("programId"))[0];
        String product =   ((String[])params.get("productId"))[0];
        String zone =     ((String[])params.get("zoneId"))[0];
        String rgroup =     ((String[])params.get("rgroupId"))[0];
        String schedule =    ((String[])params.get("scheduleId"))[0];


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

            predicate += " and facility_types.id = "+ facilityTypeId;
        }
        if (facilityName != null &&  !facilityName.equals("undefined") && !facilityName.isEmpty() ) {

            predicate += " and facilities.name = "+ facilityName;
        }

        return predicate;
    }


}
