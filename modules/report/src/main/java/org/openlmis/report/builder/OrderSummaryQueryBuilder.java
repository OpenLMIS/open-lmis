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


import org.openlmis.report.model.params.OrderReportParam;
import org.openlmis.report.model.report.OrderSummaryReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class OrderSummaryQueryBuilder {
  public static String SelectFilteredSortedPagedRecords(Map params){


      OrderReportParam filter  = (OrderReportParam)params.get("filterCriteria");
      Map sortCriteria = (Map) params.get("sortCriteria");
      String orderType =   filter.getOrderType() == null ? null : filter.getOrderType();

      //Regular Orders
      if(orderType == null || orderType.isEmpty() || orderType.equals("Regular")){

         BEGIN();

          SELECT("distinct facility_name AS facilityName, facility_code AS facilityCode, region, product_code AS productCode, product AS description, packstoship , packsize, requisition_line_item_losses_adjustments.quantity AS discrepancy");
          FROM("vw_requisition_detail");
          INNER_JOIN("orders ON orders.id = vw_requisition_detail.req_id ");
          LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments ON vw_requisition_detail.req_line_id = requisition_line_item_losses_adjustments.requisitionlineitemid");

          writePredicates(filter);
          ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, OrderSummaryReport.class,"facility_name asc"));
          return SQL();
      } else{  //Emergency orders


          BEGIN();

          SELECT("distinct facility_name AS facilityName, facility_code AS facilityCode, region, product_code AS productCode, product AS description, packstoship ,  packsize, requisition_line_item_losses_adjustments.quantity AS discrepancy");
          FROM("vw_requisition_detail");
          INNER_JOIN("orders ON orders.id = vw_requisition_detail.req_id ");
          LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments ON vw_requisition_detail.req_line_id = requisition_line_item_losses_adjustments.requisitionlineitemid");

          writePredicates(filter);
          ORDER_BY(QueryHelpers.getSortOrder(sortCriteria,OrderSummaryReport.class,"facility_name asc"));
          return SQL();
      }
  }

  private static void writePredicates(OrderReportParam filter){
    WHERE("req_status = 'RELEASED'");
    WHERE("packstoship is not null and packstoship > 0");

    if(filter.getOrderId() != null){
      WHERE("orders.id = " + filter.getOrderId());
    } else{
      WHERE("program_id = "+filter.getProgramId());
      WHERE("processing_periods_id = "+filter.getPeriodId());

      if (filter.getFacilityId() != 0 && filter.getFacilityId() != -1) {
          WHERE("facility_id = "+filter.getFacilityId());
      }

      if (filter.getProductId() != -1 && filter.getProductId() != 0) {
          WHERE("product_id ="+ filter.getProductId());
      }else if(filter.getProductId()== -1){
          WHERE("indicator_product = true");
      }
    }
  }
}
