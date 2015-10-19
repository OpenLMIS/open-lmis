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


import org.openlmis.report.model.params.OrderReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class OrderSummaryQueryBuilder {

  public static String getQuery(Map params) {


    OrderReportParam filter = (OrderReportParam) params.get("filterCriteria");
    BEGIN();

    SELECT("distinct facility_name AS facilityName, facility_code AS facilityCode, region, product_code AS productCode, product AS description, packstoship ,  packsize, requisition_line_item_losses_adjustments.quantity AS discrepancy");
    FROM("vw_requisition_detail");
    INNER_JOIN("orders ON orders.id = vw_requisition_detail.req_id ");
    LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments ON vw_requisition_detail.req_line_id = requisition_line_item_losses_adjustments.requisitionlineitemid");
    writePredicates(filter);
    ORDER_BY("facility_name asc");
    return SQL();
  }

  private static void writePredicates(OrderReportParam filter) {
    WHERE("req_status = 'RELEASED'");
    WHERE("packstoship is not null and packstoship > 0");
    WHERE("orders.id = " + filter.getOrderId());
  }
}
