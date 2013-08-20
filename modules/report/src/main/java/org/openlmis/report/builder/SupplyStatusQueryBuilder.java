package org.openlmis.report.builder;

import java.util.Map;


public class SupplyStatusQueryBuilder {

    public static String getQuery(Map params){

        String query = "select MAX(facilities.name) facility,MAX(facility_types.name) facilityType,li.productcode code,li.productcategory as category, li.product,SUM(li.beginningBalance) openingBalance, SUM(li.quantityreceived) receipts, SUM(li.quantitydispensed) issues, SUM(li.totallossesandadjustments) adjustments,  \n" +
                "    (((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments)) closingBalance," +
                " round(cast((((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments))/SUM(li.amc)::float as numeric),1) monthsOfStock, \n" +
                " SUM(li.amc) averageMonthlyConsumption       \n" +
                "    ,(SUM(li.amc) * SUM(facility_approved_products.maxmonthsofstock)) maximumStock,\n" +
                "  case when(SUM(li.amc) * SUM(facility_approved_products.maxmonthsofstock)) - (((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments)) > 0 then \n" +
                "   (SUM(li.amc) * SUM(facility_approved_products.maxmonthsofstock)) - (((SUM(li.beginningBalance) + SUM(li.quantityreceived)) -  SUM(li.quantitydispensed)) + SUM(li.totallossesandadjustments)) ELSE 0 end  reorderAmount       \n" +
                "   ,MAX(fs.facility_name) supplyingFacility,round(cast(MAX(li.maxmonthsofstock) as numeric),1) MaxMOS, round(cast(MAX(li.maxmonthsofstock) as numeric),1) minMOS  \n" +
                "    from facilities        \n" +
                "    inner join facility_types ON facilities.typeid = facility_types.id       \n" +
                "    inner join geographic_zones on geographic_zones.id = facilities.geographiczoneid       \n" +
                "    inner join requisitions r ON  r.facilityid = facilities.id         \n" +
                "    inner join requisition_line_items li ON li.rnrid = r.id         \n" +
                "    inner join products ON products.code  ::text =   li.productcode  ::text       \n" +
                "    inner join program_products ON program_products.productid = products.id \n" +
                "    inner join facility_approved_products ON facility_approved_products.facilitytypeid = facility_types.id AND facility_approved_products.programproductid = program_products.id \n" +
                "    inner join programs ON  program_products.programid = programs.id   AND  programs.id = r.programid       \n" +
                "    inner join programs_supported ON  programs.id = programs_supported.programid   AND   facilities.id = programs_supported.facilityid         \n" +
                "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityid         \n" +
                "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid         \n" +
                "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id         \n" +
                "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.programid         \n" +
                "    inner join processing_periods ON processing_periods.scheduleid = processing_schedules.id  \n" +
                "    left outer join vw_program_facility_supplier fs ON fs.supervisory_node_id = requisition_groups.supervisorynodeid AND fs.program_id = programs.id \n" +

                writePredicates((Map) params.get("filterCriteria"))+ "\n"+

                "group by facilities.name,li.productcode, li.product, li.productcategory ,requisition_groups.id \n" +
                " order by " + QueryHelpers.getSortOrder(params, "facilities.name asc,li.productcode asc,  li.product asc, li.productcategory asc , requisition_groups.id asc");
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

    public static String getTotalCount(Map params){
        String query = "select count(*) \n"+
                "    from facilities        \n" +
                "    inner join facility_types ON facilities.typeid = facility_types.id       \n" +
                "    inner join geographic_zones on geographic_zones.id = facilities.geographiczoneid       \n" +
                "    inner join requisitions r ON  r.facilityid = facilities.id         \n" +
                "    inner join requisition_line_items li ON li.rnrid = r.id         \n" +
                "    inner join products ON products.code  ::text =   li.productcode  ::text       \n" +
                "    inner join program_products ON program_products.productid = products.id \n" +
                "    inner join facility_approved_products ON facility_approved_products.facilitytypeid = facility_types.id AND facility_approved_products.programproductid = program_products.id \n" +
                "    inner join programs ON  program_products.programid = programs.id   AND  programs.id = r.programid       \n" +
                "    inner join programs_supported ON  programs.id = programs_supported.programid   AND   facilities.id = programs_supported.facilityid         \n" +
                "    inner join requisition_group_members ON facilities.id = requisition_group_members.facilityid         \n" +
                "    inner join requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid         \n" +
                "    inner join requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id         \n" +
                "    inner join processing_schedules ON processing_schedules.id = requisition_group_program_schedules.programid         \n" +
                "    inner join processing_periods ON processing_periods.scheduleid = processing_schedules.id  \n" +
                "    left outer join vw_program_facility_supplier fs ON fs.supervisory_node_id = requisition_groups.supervisorynodeid AND fs.program_id = programs.id \n" +

                writePredicates(params);

        return query;
    }




}
