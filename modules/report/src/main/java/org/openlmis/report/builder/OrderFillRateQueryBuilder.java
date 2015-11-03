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

import org.openlmis.report.model.params.OrderFillRateReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class OrderFillRateQueryBuilder {

  public static String getQuery(Map params) {

    Long userId = (Long) params.get("userId");
    OrderFillRateReportParam queryParam = (OrderFillRateReportParam) params.get("filterCriteria");
    return getQueryString(queryParam, userId);
  }

  private static void writePredicates(OrderFillRateReportParam param) {

    WHERE(programIsFilteredBy("programid"));
    WHERE(periodIsFilteredBy("periodid"));

    if (param.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("gz"));
    }

    if(param.getFacilityType() != 0){
      WHERE(facilityTypeIsFilteredBy("facilityTypeId"));
    }

    if(param.getFacility() != 0){
      WHERE(facilityIsFilteredBy("facilityId"));
    }

    if(param.getProductCategory() != 0){
      WHERE(productCategoryIsFilteredBy("productCategoryId"));
    }

    if (multiProductFilterBy(param.getProducts(), "productId", "tracer") != null) {
      WHERE(multiProductFilterBy(param.getProducts(), "productId", "tracer"));
    }


  }

  private static String getQueryString(OrderFillRateReportParam param, Long userId) {
    BEGIN();
    SELECT_DISTINCT("facilityname facility,quantityapproved as Approved,quantityreceived receipts ,productcode, product, " +
        " CASE WHEN COALESCE(quantityapproved, 0::numeric) = 0::numeric THEN 0::numeric\n" +
        "    ELSE COALESCE(quantityreceived,0 )/ COALESCE(quantityapproved,0) * 100::numeric\n" +
        "                                     END AS item_fill_rate ");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = vw_order_fill_rate.zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program} )");
    WHERE(" status in ('RELEASED') and totalproductsapproved > 0 ");
    writePredicates(param);
    GROUP_BY("product, approved, " +
        "  quantityreceived,  productcode, " +
        "  facilityname ");
    ORDER_BY("facilityname");
    return SQL();
  }

  public static String getTotalProductsReceived(OrderFillRateReportParam param) {

    BEGIN();
    SELECT("count(totalproductsreceived) quantityreceived");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("totalproductsreceived>0 and totalproductsapproved >0  and status in ('RELEASED') and periodId = #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId = #{filterCriteria.facility}");
    writePredicates(param);
    GROUP_BY("totalproductsreceived");
    return SQL();
  }


  public static String getTotalProductsOrdered(OrderFillRateReportParam params) {

    BEGIN();
    SELECT("count(totalproductsapproved) quantityapproved");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("totalproductsapproved > 0  and status in ('RELEASED') and periodId= #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId= #{filterCriteria.facility} ");
    writePredicates(params);
    return SQL();

  }


  public static String getSummaryQuery(OrderFillRateReportParam params) {

    BEGIN();
    SELECT("count(totalproductsreceived) quantityreceived");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("totalproductsreceived>0 and totalproductsapproved > 0 and  status in ('RELEASED') and periodId= #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId= #{filterCriteria.facility}");
    writePredicates(params);
    GROUP_BY("totalproductsreceived");
    String query = SQL();
    RESET();
    BEGIN();
    SELECT("count(totalproductsapproved) quantityapproved");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("status in ('RELEASED') and totalproductsapproved > 0 and periodId= #{filterCriteria.period} and programId= #{filterCriteria.program} and facilityId= #{filterCriteria.facility}");
    writePredicates(params);
    query += " UNION " + SQL();
    return query;
  }
}