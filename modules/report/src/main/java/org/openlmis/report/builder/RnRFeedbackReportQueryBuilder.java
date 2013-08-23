package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Wolde
 * Date: 8/21/13
 * Time: 3:38 AM
 */
public class RnRFeedbackReportQueryBuilder {
    public static String getQuery(Map params){
        Map filterCriteria = (Map) params.get("filterCriteria");
        String query = "SELECT distinct \n" +
                "vw_requisition_detail.facility_code AS facilityCode,\n" +
                "vw_requisition_detail.facility_name AS facility,\n" +
                "vw_requisition_detail.productcode AS productCode,\n" +
                "vw_requisition_detail.product AS product,\n" +
                "vw_requisition_detail.beginningBalance,\n" +
                "vw_requisition_detail.quantityreceived AS totalQuantityReceived,\n" +
                "vw_requisition_detail.quantitydispensed AS totalQuantityDispensed,\n" +
                "vw_requisition_detail.stockinhand AS physicalCount,\n" +
                "vw_requisition_detail.totallossesandadjustments AS adjustments,\n" +
                "CASE stockoutdays when 30 then 0 else CASE amc when 0 then 0 else (vw_requisition_detail.amc * 30) / (30- stockoutdays) end end AS adjustedAMC,\n" +
                "vw_requisition_detail.quantityrequested AS orderQuantity, -- TODO: fix it \n" +
                "0 AS quantitySupplied, -- TODO: fix it \n" +
                "'unit' unit, --TODO: fix it \n" +
                "0 newEOP, --TODO: fix it \n" +
                "0 maximumStock, --TODO: fix it \n" +
                "0 emergencyOrder --TODO: fix it \n" +
                "FROM vw_requisition_detail \n" +
                writePredicates(filterCriteria)
               ;

        return query;
    }
    private static String writePredicates(Map params){

        String predicate = "WHERE ";
        String facilityTypeId =  params.get("facilityTypeId") == null ? null :((String[])params.get("facilityTypeId"))[0];
        String facilityName = params.get("facilityName") == null ? null : ((String[])params.get("facilityName"))[0];
        String period =    params.get("periodId") == null ? null : ((String[])params.get("periodId"))[0];
        String program =   params.get("programId") == null ? null : ((String[])params.get("programId"))[0];
        String product =   params.get("productId") == null ? null : ((String[])params.get("productId"))[0];
        String rgroup =     params.get("rgroupId") == null ? null : ((String[])params.get("rgroupId"))[0];
        String schedule = params.get("scheduleId") == null ? null : ((String[])params.get("scheduleId"))[0];

        predicate += "  processing_periods_id = "+ period;

        predicate += " and program_id = "+ program;

        predicate += " and processing_schedules_id = "+ schedule;

        predicate += " and facility_name = '"+ facilityName +"'";

        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and product_id = "+ product;
        }

        if (rgroup != null &&  !rgroup.equals("undefined") && !rgroup.isEmpty() && !rgroup.equals("0") &&  !rgroup.equals("-1")) {

            predicate += " and requisition_group_id = "+ rgroup;
        }
        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {

            predicate += " and facility_type_id = "+ facilityTypeId;
        }

        return predicate;
    }
}
