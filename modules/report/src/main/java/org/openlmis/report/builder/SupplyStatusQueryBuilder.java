package org.openlmis.report.builder;

import org.openlmis.report.model.report.SupplyStatusReport;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Wolde
 * Date: 8/24/13
 * Time: 3:45 AM
 */
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
                "  li_maxmonthsofstock MaxMOS," +
                "  li_maxmonthsofstock  minMOS   \n " +
                " from vw_supply_status \n"+
                writePredicates(filterCriteria)+ "\n"+

                " order by " + QueryHelpers.getSortOrder(filterCriteria,SupplyStatusReport.class, "facility asc,li_productcode asc,  li_product asc, li_productcategory asc ");
        return query;
    }

    private static String writePredicates(Map params){
        String predicate = "WHERE r_status = 'RELEASED' ";
        String facilityTypeId =  params.get("facilityTypeId") == null ? null :((String[])params.get("facilityTypeId"))[0];
        String facilityId = params.get("facilityId") == null ? null : ((String[])params.get("facilityId"))[0];
        String period =    params.get("periodId") == null ? null : ((String[])params.get("periodId"))[0];
        String program =   params.get("programId") == null ? null : ((String[])params.get("programId"))[0];
        String product =   params.get("productId") == null ? null : ((String[])params.get("productId"))[0];
        String zone =     params.get("zoneId") == null ? null : ((String[])params.get("zoneId"))[0];
        String rgroup =     params.get("rgroupId") == null ? null : ((String[])params.get("rgroupId"))[0];
        String schedule = params.get("scheduleId") == null ? null : ((String[])params.get("scheduleId"))[0];

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
        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {

            predicate += " and ft_id = "+ facilityTypeId;
        }


        return predicate;
    }

}
