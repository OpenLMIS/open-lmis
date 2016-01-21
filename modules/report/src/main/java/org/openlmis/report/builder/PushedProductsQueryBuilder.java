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

public class PushedProductsQueryBuilder {


  public static String getQueryForPushedItems(Map params) {
    OrderFillRateReportParam filters = (OrderFillRateReportParam) params.get("filterParam");
    return getProductsPushedQuery(filters);
  }

  private static String getProductsPushedQuery(OrderFillRateReportParam filter) {

    BEGIN();
    SELECT_DISTINCT("product,productcode,sum(quantityApproved) approved,sum(quantityreceived) receipts");
    FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
    WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program}) ");
    WHERE("totalproductsapproved = 0 and quantityreceived > 1\n" +
        " and  status in ('RELEASED') and periodid = #{filterCriteria.period} " +
        "and facilityId= #{filterCriteria.facility} and programid = #{filterCriteria.program}");
    writePredicates(filter);
    GROUP_BY("product,productcode");
    ORDER_BY("product");
    String sql = SQL();
    return sql;
  }

  private static void writePredicates(OrderFillRateReportParam filter) {
    WHERE(programIsFilteredBy("programid"));
    WHERE(periodIsFilteredBy("periodid"));

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("gz"));
    }

    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facilityTypeId"));
    }

    if (filter.getFacility() != 0) {
      WHERE(facilityIsFilteredBy("facilityId"));
    }

    if (filter.getProductCategory() != 0) {
      WHERE(productCategoryIsFilteredBy("productCategoryId"));
    }

    if (multiProductFilterBy(filter.getProducts(), "productId", "tracer") != null) {
      WHERE(multiProductFilterBy(filter.getProducts(), "productId", "tracer"));
    }
  }

}
