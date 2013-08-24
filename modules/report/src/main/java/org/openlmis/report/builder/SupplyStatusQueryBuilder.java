/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import java.util.Map;


public class SupplyStatusQueryBuilder {

    public static String getQuery(Map params){

        String query = "SELECT facility,facility_type_name facilityType,li_productcode code,li_productcategory category, li_product product, li_beginningbalance openingBalance,\n" +
                "  li_quantityreceived receipts," +
                "  li_quantitydispensed issues," +
                "  li_totallossesandadjustments adjustments," +
                "  li_stockinhand closingBalance," +
                "  CASE li_amc when 0 then 0 else li_stockinhand/li_amc end monthsOfStock," +
                "  li_amc averageMonthlyConsumption," +
                "  li_amc * fp_maxmonthsofstock maximumStock, " +
                "  li_calculatedorderquantity reorderAmount, " +
                "  r_supplyingfacilityid supplyingFacility," +
                "  li_maxmonthsofstock MaxMOS," +
                "  li_maxmonthsofstock  minMOS   \n " +
                " from vw_supply_status \n"+
                writePredicates((Map) params.get("filterCriteria"))+ "\n"+

               // "group by facilities.name,li.productcode, li.product, li.productcategory ,requisition_groups.id \n" +
                " order by " + QueryHelpers.getSortOrder(params, "facility asc,li_productcode asc,  li_product asc, li_productcategory asc ");
            return query;
    }

    public static String getSupplyStatusQuery(Map params){
        String query = "SELECT facility,facility_type_name facilityType,li_productcode code,li_productcategory category, li_product product, li_beginningbalance openingBalance," +
                "  li_quantityreceived receipts," +
                "  li_quantitydispensed issues," +
                "  li_totallossesandadjustments adjustments," +
                "  li_stockinhand closingBalance," +
                "  CASE li_amc when 0 then 0 else li_stockinhand/li_amc end monthsOfStock," +
                "  li_amc averageMonthlyConsumption," +
                "  li_amc * fp_maxmonthsofstock maximumStock, " +
                "  li_calculatedorderquantity reorderAmount, " +
                "  r_supplyingfacilityid supplyingFacility," +
                "  li_maxmonthsofstock MaxMOS," +
                "  li_maxmonthsofstock  minMOS   \n " +
                " from vw_supply_status \n"+
                writePredicates((Map) params.get("filterCriteria"))+ "\n"+

                // "group by facilities.name,li.productcode, li.product, li.productcategory ,requisition_groups.id \n" +
                " order by " + QueryHelpers.getSortOrder(params, "facility asc,li_productcode asc,  li_product asc, li_productcategory asc ");
        return query;
    }

    private static String writePredicates(Map params){
        String predicate = "WHERE r_status = 'RELEASED' ";
        String facilityTypeId =  params.get("facilityTypeId") == null ? null :((String[])params.get("facilityTypeId"))[0];
        String facilityName = params.get("facilityName") == null ? null : ((String[])params.get("facilityName"))[0];
        String period =    params.get("periodId") == null ? null : ((String[])params.get("periodId"))[0];
        String program =   params.get("programId") == null ? null : ((String[])params.get("programId"))[0];
        String product =   params.get("productId") == null ? null : ((String[])params.get("productId"))[0];
        String zone =     params.get("zoneId") == null ? null : ((String[])params.get("zoneId"))[0];
        String rgroup =     params.get("rgroupId") == null ? null : ((String[])params.get("rgroupId"))[0];
        String schedule = params.get("scheduleId") == null ? null : ((String[])params.get("scheduleId"))[0];

        predicate += " and pp_id = "+ period;

        predicate += " and pg_id = "+ program;

        predicate += " and ps_id = "+ schedule;

        //if (facilityName != null &&  !facilityName.equals("undefined") && !facilityName.isEmpty() ) {

            predicate += " and LOWER(facility) = '"+ facilityName.toLowerCase() +"'";
        //}

        if (zone != null &&  !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {

            predicate += " and f_zoneid = "+ zone;
        }
        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and p_id = "+ product;
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
