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

import org.openlmis.report.model.params.SupplyStatusReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class SupplyStatusQueryBuilder {

  public String getSupplyStatus(Map params) {

    SupplyStatusReportParam filterCriteria = (SupplyStatusReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT(" facility,facility_type_name facilityType,li_productcode code,li_productcategory category, li_product product, li_beginningbalance openingBalance");
    SELECT("  li_quantityreceived receipts");
    SELECT("  li_quantitydispensed issues," +
      "  li_totallossesandadjustments adjustments," +
      "  li_stockinhand closingBalance," +
      "  CASE li_amc when 0 then 0 else ROUND((li_stockinhand::NUMERIC / li_amc)::NUMERIC,2) end monthsOfStock," +
      "  li_amc averageMonthlyConsumption," +
      "  li_amc * fp_maxmonthsofstock maximumStock, " +
      "  li_calculatedorderquantity reorderAmount, " +
      "  supplyingfacility supplyingFacility," +
      "  fp_maxmonthsofstock MaxMOS," +
      "  fp_minmonthsofstock  minMOS   \n ");
    FROM(" vw_supply_status join vw_districts d on d.district_id = f_zoneid ");
    WHERE(rnrStatusFilteredBy("r_status", filterCriteria.getAcceptedRnrStatuses()));
    WHERE(userHasPermissionOnFacilityBy("f_id"));
    WHERE(periodIsFilteredBy("pp_id"));
    WHERE(programIsFilteredBy("pg_id"));
    WHERE(facilityIsFilteredBy("f_id"));
    if (filterCriteria.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("d"));
    }
    if (filterCriteria.getProduct() > 0) {
      WHERE(productFilteredBy("p_id"));
    } else if (filterCriteria.getProduct() != null && filterCriteria.getProduct().equals(-1)) {
      WHERE(" indicator_product = true");
    }

    ORDER_BY(" facility asc,li_productcode asc,  li_product asc, li_productcategory asc ");
    return SQL();
  }


}
