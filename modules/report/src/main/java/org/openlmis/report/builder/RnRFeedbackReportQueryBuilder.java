/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.report.RnRFeedbackReport;
import org.openlmis.report.model.filter.RnRFeedbackReportFilter;


import java.util.Calendar;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: mahmed
 * Date: 6/02/13
 * Time: 3:20 PM
 */
public class RnRFeedbackReportQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){


        RnRFeedbackReportFilter filter  = (RnRFeedbackReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("sortCriteria");
        String orderType =   filter.getOrderType() == null ? null : filter.getOrderType();

        //Regular Orders
        if(orderType == null || orderType.isEmpty() || orderType.equals("Regular")){

            BEGIN();
            SELECT("distinct facility_code AS facilityCode, facility_name AS facility, productcode AS productCode, product AS product, beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, stockinhand AS physicalCount, totallossesandadjustments AS adjustments, CASE stockoutdays when 30 then 0 else CASE amc when 0 then 0 else (vw_requisition_detail.amc * 30) / (30- stockoutdays) end end AS adjustedAMC, quantityrequested AS orderQuantity, 0 AS quantitySupplied, dispensingunit AS unit, amc * nominaleop AS newEOP, maxstockquantity maximumStock, 0 emergencyOrder");
            FROM("vw_requisition_detail");
            writePredicates(filter);
            ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, RnRFeedbackReport.class,"productcode asc,facility_name asc"));

            return SQL();

        } else{  //Emergency orders


            BEGIN();
            SELECT("distinct facility_code AS facilityCode, facility_name AS facility, productcode AS productCode, product AS product, beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, stockinhand AS physicalCount, totallossesandadjustments AS adjustments, CASE stockoutdays when 30 then 0 else CASE amc when 0 then 0 else (vw_requisition_detail.amc * 30) / (30- stockoutdays) end end AS adjustedAMC, quantityrequested AS orderQuantity, 0 AS quantitySupplied,dispensingunit AS unit,amc * nominaleop AS newEOP, maxstockquantity maximumStock, 0 emergencyOrder");
            FROM("vw_requisition_detail");
            writePredicates(filter);
            ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, RnRFeedbackReport.class,"productcode asc,facility_name asc"));

            return SQL();
        }
    }

    private static void writePredicates(RnRFeedbackReportFilter  filter){

        WHERE("program_id = "+filter.getProgramId());
        WHERE("facility_id = "+filter.getFacilityId());
        WHERE("processing_periods_id = "+filter.getPeriodId());
        WHERE("req_status in ('APPROVED','RELEASED')");

        if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
            WHERE("zone_id = "+filter.getZoneId());
        }
        if (filter.getProductId() != -1 && filter.getProductId() != 0) {
            WHERE("product_id ="+ filter.getProductId());
        }else if(filter.getProductId()== -1){
            WHERE("indicator_product = true");
        }


    }
}