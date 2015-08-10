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


import org.openlmis.report.model.params.DistrictConsumptionReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class DistrictConsumptionQueryBuilder {

  public static String getQuery(Map params) {

    DistrictConsumptionReportParam filter = (DistrictConsumptionReportParam) params.get("filterCriteria");


    BEGIN();
    SELECT("p.code");
    SELECT("p.primaryName || ' (' || coalesce(p.dispensingunit, '-') || ')' as product");
    SELECT("d.district_name as district");
    SELECT("sum(li.quantityDispensed) dispensed");
    SELECT("sum(li.normalizedConsumption) consumption");
    SELECT("ceil(sum(li.quantityDispensed) / (sum(li.packsize)/count(li.productCode))::float) consumptionInPacks");
    SELECT("ceil(sum(li.normalizedConsumption) / (sum(li.packsize)/count(li.productCode))::float) adjustedConsumptionInPacks ");
    FROM("requisition_line_items li");
    INNER_JOIN("requisitions r on r.id = li.rnrid");
    INNER_JOIN("facilities f on r.facilityId = f.id ");
    INNER_JOIN("vw_districts d on d.district_id = f.geographicZoneId ");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityId = r.facilityId");
    INNER_JOIN("processing_periods pp on pp.id = r.periodId");
    INNER_JOIN("products p on p.code::text = li.productCode::text");
    INNER_JOIN("program_products ppg on ppg.programId = r.programId and ppg.productId = p.id");


    WHERE(programIsFilteredBy("r.programId"));
    WHERE(periodIsFilteredBy("r.periodId"));
    WHERE(userHasPermissionOnFacilityBy("r.facilityId"));
    WHERE(rnrStatusFilteredBy("r.status", filter.getAcceptedRnrStatuses()));

    if(filter.getProductCategory() != 0){
      WHERE( productCategoryIsFilteredBy("ppg.productCategoryId"));
    }

    WHERE(productFilteredBy("p.id"));

    if (filter.getZone() != 0) {
      WHERE( geoZoneIsFilteredBy("d") );
    }

    GROUP_BY("p.code, p.primaryName, p.dispensingunit, d.district_name");
    return String.format( "select sq.*, " +
        " (sq.consumption / sum(sq.consumption) over ()) * 100 as totalPercentage " +
        "from ( %s ) as sq " +
        "order by coalesce(sq.consumption,0) desc", SQL());
  }

}
