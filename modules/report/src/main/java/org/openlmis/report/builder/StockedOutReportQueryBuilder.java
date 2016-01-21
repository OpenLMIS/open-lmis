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

import org.openlmis.report.model.params.StockedOutReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.geoZoneIsFilteredBy;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.multiProductFilterBy;


public class StockedOutReportQueryBuilder {

  public static String getQuery(Map params) {
    StockedOutReportParam filter = (StockedOutReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT("DISTINCT supplyingfacility, facilitycode, productCode, facility, product, facilitytypename, location, processing_period_name,stockoutdays");
      FROM("vw_stock_status join vw_districts d on gz_id = d.district_id");
    WHERE("status = 'SO'");
    WHERE("reported_figures > 0");
    WHERE(programIsFilteredBy("programId"));
    WHERE(periodIsFilteredBy("periodId"));
    WHERE(userHasPermissionOnFacilityBy("facility_id"));
    WHERE(rnrStatusFilteredBy("req_status", filter.getAcceptedRnrStatuses()));

    if (filter.getProductCategory() != 0) {
      WHERE(productCategoryIsFilteredBy("categoryId"));
    }

    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facilityTypeId"));
    }

    if (multiProductFilterBy(filter.getProducts(), "productId", "indicator_product") != null) {
      WHERE(multiProductFilterBy(filter.getProducts(), "productId", "indicator_product"));
    }

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("d"));
    }
    ORDER_BY("supplyingFacility asc, facility asc, product asc");
    // copy the sql over to a variable, this makes the debugging much more possible.
    return SQL();
  }


}
