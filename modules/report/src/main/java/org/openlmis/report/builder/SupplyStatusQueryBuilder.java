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

import org.openlmis.report.model.report.SupplyStatusReport;

import java.util.Map;

public class SupplyStatusQueryBuilder {

    public String getSupplyStatus(Map params){

        Map filterCriteria = (Map) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");

        String query = "SELECT facility,facility_type_name facilityType,li_productcode code,li_productcategory category, li_product product, li_beginningbalance openingBalance,\n" +
                "  li_quantityreceived receipts," +
                "  li_quantitydispensed issues," +
                "  li_totallossesandadjustments adjustments," +
                "  li_stockinhand closingBalance," +
                "  CASE li_amc when 0 then 0 else li_stockinhand / li_amc end monthsOfStock," +
                "  li_amc averageMonthlyConsumption," +
                "  li_amc * fp_maxmonthsofstock maximumStock, " +
                "  li_calculatedorderquantity reorderAmount, " +
                "  supplyingfacility supplyingFacility," +
                "  fp_maxmonthsofstock MaxMOS," +
                "  fp_minmonthsofstock  minMOS   \n " +
                " from vw_supply_status join vw_districts d on d.district_id = f_zoneid \n"+
                writePredicates(filterCriteria, userId)+ "\n"+

                " order by " + QueryHelpers.getSortOrder(filterCriteria,SupplyStatusReport.class, "facility asc,li_productcode asc,  li_product asc, li_productcategory asc ");
        return query;
    }

    private static String writePredicates(Map params, Long userId){

        String predicate = "WHERE r_status in ('APPROVED', 'RELEASED') ";
        String facilityTypeId =  params.get("facilityType") == null ? null :((String[])params.get("facilityType"))[0];
        String facilityId = params.get("facility") == null ? null : ((String[])params.get("facility"))[0];
        String period =    params.get("period") == null ? null : ((String[])params.get("period"))[0];
        String program =   params.get("program") == null ? null : ((String[])params.get("program"))[0];
        String product =   params.get("product") == null ? null : ((String[])params.get("product"))[0];
        String zone =     params.get("zone") == null ? null : ((String[])params.get("zone"))[0];
        String rgroup =     params.get("requisitionGroup") == null ? null : ((String[])params.get("requisitionGroup"))[0];
        String schedule = params.get("schedule") == null ? null : ((String[])params.get("schedule"))[0];
        predicate += " and f_id in (select facility_id from vw_user_facilities where user_id = " + userId + " and program_id = " + program + ")";
        predicate += " and pp_id = "+ period;

        predicate += " and pg_id = "+ program;

        predicate += " and ps_id = "+ schedule;

        predicate += " and f_id = "+ facilityId;

        if (zone != null && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {

            predicate += " and (d.district_id = " + zone + " or d.zone_id =  " + zone + "  or d.region_id =  " + zone + "  or d.parent =  " + zone + " ) ";
        }
        if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {

            predicate += " and p_id = "+ product;

        }else if(product != null &&  !product.equals("undefined") && !product.isEmpty() && product.equals("0")){
            predicate += " and indicator_product = true";
        }

        if (rgroup != null &&  !rgroup.equals("undefined") && !rgroup.isEmpty() && !rgroup.equals("0") &&  !rgroup.equals("-1")) {

            predicate += " and rgm_id = "+ rgroup;
        }


        return predicate;
    }

}
