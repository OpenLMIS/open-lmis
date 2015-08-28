/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.builder;

import org.openlmis.report.model.params.RnRFeedbackReportParam;


import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class RnRFeedbackReportQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){


        RnRFeedbackReportParam filter  = (RnRFeedbackReportParam)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("sortCriteria");
        Long userId = (Long) params.get("userId");

        String orderType =   filter.getOrderType() == null ? null : filter.getOrderType();

        //Regular Orders
        if(orderType == null || orderType.isEmpty() || orderType.equals("Regular")){
            // main product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, productcode as productCode, product, productcode as productCodeMain, product as productMain, dispensingunit AS unit, beginningbalance as beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, totallossesandadjustments AS adjustments, stockinhand AS physicalCount, amc AS adjustedAMC, amc * nominaleop AS newEOP, maxstockquantity AS maximumStock, quantityrequested AS orderQuantity, quantityshipped AS quantitySupplied, quantity_shipped_total AS totalQuantityShipped, 0 AS emergencyOrder, 0 AS productIndex, err_open_balance, err_qty_required, err_qty_received, err_qty_stockinhand");
          FROM("vw_rnr_feedback join facilities f on f.id = facility_id join vw_districts d on d.district_id = f.geographicZoneId ");
            WHERE("(substitutedproductcode is null or (productcode is not null and substitutedproductcode is not null))");
          WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
            writePredicates(filter);
            String query = SQL();
            RESET();
            //substitute product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, substitutedproductcode as productCode, substitutedproductname as product, productcode as productCodeMain, product as productMain, null AS unit, null as beginningBalance, null as totalQuantityReceived, null AS totalQuantityDispensed, null as adjustments, null AS physicalCount, null AS adjustedAMC, null AS newEOP, null AS maximumStock, null AS orderQuantity, substitutedproductquantityshipped quantitySupplied, null AS totalQuantityShipped, 0 AS emergencyOrder, 1 AS productIndex, 0 as err_open_balance, 0 as err_qty_required, 0 as err_qty_received, 0 as err_qty_stockinhand");
          FROM("vw_rnr_feedback join facilities f on f.id = facility_id join vw_districts d on d.district_id = f.geographicZoneId ");
            WHERE("substitutedproductcode is not null");
          WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
            writePredicates(filter);
            query += " UNION " + SQL() + " order by facility, productcodeMain, productIndex";
            return query;

        } else{  //Emergency orders

            // main product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, productcode as productCode, product, productcode as productCodeMain, dispensingunit AS unit, beginningbalance as beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, totallossesandadjustments AS adjustments, stockinhand AS physicalCount, amc AS adjustedAMC, amc * nominaleop AS newEOP, maxstockquantity AS maximumStock, quantityrequested AS orderQuantity, quantityshipped AS quantitySupplied, quantity_shipped_total AS totalQuantityShipped, 1 AS emergencyOrder, 0 AS productIndex, err_open_balance, err_qty_required, err_qty_received, err_qty_stockinhand");
          FROM("vw_rnr_feedback join facilities f on f.id = facility_id join vw_districts d on d.district_id = f.geographicZoneId ");
          WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
            WHERE("(substitutedProductCode is null or (productcode is not null and substitutedProductCode is not null))");
            writePredicates(filter);
            String query = SQL();
            RESET();
            //substitute product
            BEGIN();
            SELECT("facility_code AS facilityCode, facility_name AS facility, productcode as productCode, product, productcode as productCodeMain, dispensingunit AS unit, beginningbalance as beginningBalance, quantityreceived AS totalQuantityReceived, quantitydispensed AS totalQuantityDispensed, totallossesandadjustments AS adjustments, stockinhand AS physicalCount, amc AS adjustedAMC, amc * nominaleop AS newEOP, maxstockquantity AS maximumStock, quantityrequested AS orderQuantity, quantityshipped AS quantitySupplied, quantity_shipped_total AS totalQuantityShipped, 1 AS emergencyOrder, 0 AS productIndex, err_open_balance, err_qty_required, err_qty_received, err_qty_stockinhand");

            FROM("vw_rnr_feedback join facilities f on f.id = facility_id join vw_districts d on d.district_id = f.geographicZoneId ");
          WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
            WHERE("substitutedProductCode is not null");
            writePredicates(filter);
            query += " UNION " + SQL() + " order by facility, productMain, productIndex";
            return query;
        }
    }

    private static void writePredicates(RnRFeedbackReportParam filter){
      WHERE("req_status = 'RELEASED'");
      WHERE("program_id = "+filter.getProgramId());
      if(filter.getRgroupId() != 0){
          WHERE("requisition_group_id ="+ filter.getRgroupId());
      }

      if (filter.getFacilityId() != 0 && filter.getFacilityId() != -1) {
          WHERE("facility_id = "+filter.getFacilityId());
      }

      if (filter.getZoneId() > 0) {
        WHERE("(d.district_id = #{filterCriteria.zoneId} or d.zone_id = #{filterCriteria.zoneId} or d.region_id = #{filterCriteria.zoneId} or d.parent = #{filterCriteria.zoneId})");
      }

      WHERE("processing_periods_id = "+filter.getPeriodId());
      if (filter.getProductId() != -1 && filter.getProductId() != 0) {
          WHERE("product_id ="+ filter.getProductId());
      }else if(filter.getProductId()== -1){
          WHERE("indicator_product = true");
      }


    }
}