package org.openlmis.report.builder;

import org.openlmis.report.model.report.SupplyStatusReport;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SupplyStatusQueryBuilder {

    public String getSupplyStatus(Map params){
        Map filterCriteria = (Map) params.get("filterCriteria");
        String query = "SELECT facility,facility_type_name facilityType,li_productcode code,li_productcategory category, li_product product, li_beginningbalance openingBalance,\n" +
                "  li_quantityreceived receipts," +
                "  li_quantitydispensed issues," +
                "  li_totallossesandadjustments adjustments," +
                "  li_stockinhand closingBalance," +
                "  CASE li_amc when 0 then 0 else li_stockinhand/li_amc end monthsOfStock," +
                "  li_amc averageMonthlyConsumption," +
                "  li_amc * fp_maxmonthsofstock maximumStock, " +
                "  li_calculatedorderquantity reorderAmount, " +
                "  supplyingfacility supplyingFacility," +
                "  fp_maxmonthsofstock MaxMOS," +
                "  fp_minmonthsofstock  minMOS   \n " +
                " from vw_supply_status \n"+
                writePredicates(filterCriteria)+ "\n"+

                " order by " + QueryHelpers.getSortOrder(filterCriteria,SupplyStatusReport.class, "facility asc,li_productcode asc,  li_product asc, li_productcategory asc ");
        return query;
    }

    private static String writePredicates(Map params){
        String predicate = "WHERE r_status in ('APPROVED', 'RELEASED') ";
        String facilityTypeId =  params.get("facilityType") == null ? null :((String[])params.get("facilityType"))[0];
        String facilityId = params.get("facility") == null ? null : ((String[])params.get("facility"))[0];
        String period =    params.get("period") == null ? null : ((String[])params.get("period"))[0];
        String program =   params.get("program") == null ? null : ((String[])params.get("program"))[0];
        String product =   params.get("product") == null ? null : ((String[])params.get("product"))[0];
        String zone =     params.get("zone") == null ? null : ((String[])params.get("zone"))[0];
        String rgroup =     params.get("rgroup") == null ? null : ((String[])params.get("rgroup"))[0];
        String schedule = params.get("schedule") == null ? null : ((String[])params.get("schedule"))[0];

        predicate += " and pp_id = "+ period;

        predicate += " and pg_id = "+ program;

        predicate += " and ps_id = "+ schedule;

        predicate += " and f_id = "+ facilityId;

        if (zone != null &&  !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {

            predicate += " and f_zoneid = "+ zone;
        }
        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and p_id = "+ product;

        }else if(product != null &&  !product.equals("undefined") && !product.isEmpty() && product.equals("-1")){
            predicate += " and indicator_product = true";
        }

        if (rgroup != null &&  !rgroup.equals("undefined") && !rgroup.isEmpty() && !rgroup.equals("0") &&  !rgroup.equals("-1")) {

            predicate += " and rgm_id = "+ rgroup;
        }


        return predicate;
    }

}
