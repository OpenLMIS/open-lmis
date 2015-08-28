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

import org.openlmis.report.model.params.RegimenSummaryReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class RegimenSummaryQueryBuilder {

  public static String getRegimenSummaryData(Map params) {

    RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");

    BEGIN();

    SELECT("facilitycode,facilityType facilityTypeName,facilityName,district,region,zone, regimen ");
    SELECT(" SUM(patientsontreatment) patientsontreatment ");
    SELECT(" SUM(patientstoinitiatetreatment) patientstoinitiatetreatment ");
    SELECT(" SUM(patientsstoppedtreatment) patientsstoppedtreatment ");
    FROM(" vw_regimen_district_distribution join vw_districts d on d.district_id = districtId ");
    WHERE(programIsFilteredBy("programId"));
    WHERE(periodIsFilteredBy("periodId"));
    WHERE(userHasPermissionOnFacilityBy("facilityId"));
    WHERE(rnrStatusFilteredBy("status", filter.getAcceptedRnrStatuses()));

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("d"));
    }
    if (filter.getRegimenCategory() != 0) {
      WHERE(regimenIsFilteredBy("categoryId"));
    }
    if (filter.getRegimen() != 0) {
      WHERE(regimenIsFilteredBy("regimenId"));
    }
    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facilityTypeId"));
    }
    if (filter.getFacility() != 0) {
      WHERE(facilityIsFilteredBy("facilityId"));
    }
    GROUP_BY("regimen, district, facilityName, facilityType, facilityCode, region, zone ");
    ORDER_BY("region, regimen");
    return SQL();
  }


  public static String getRegimenDistributionData(Map params) {
    RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");

    BEGIN();
    SELECT("regimen, district, " +
      " SUM(patientsontreatment) patientsontreatment, " +
      " SUM(patientstoinitiatetreatment) patientstoinitiatetreatment, " +
      " SUM(patientsstoppedtreatment) patientsstoppedtreatment ");

    FROM("vw_regimen_district_distribution join vw_districts d on d.district_id = districtId ");
    WHERE(programIsFilteredBy("programId"));
    WHERE(periodIsFilteredBy("periodId"));
    WHERE(userHasPermissionOnFacilityBy("facilityId"));
    WHERE(rnrStatusFilteredBy("status", filter.getAcceptedRnrStatuses()));

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("d"));
    }
    if (filter.getRegimenCategory() != 0) {
      WHERE(regimenCategoryIsFilteredBy("categoryId"));
    }
    if (filter.getRegimen() != 0) {
      WHERE(regimenIsFilteredBy("regimenId"));
    }
    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facilityTypeId"));
    }
    if (filter.getFacility() != 0) {
      WHERE(facilityIsFilteredBy("facilityId"));
    }
    GROUP_BY("regimen,district");
    ORDER_BY("regimen,district");
    return SQL();
  }


  public static String getAggregateRegimenDistribution(Map params) {
    RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
    BEGIN();
    SELECT(
      " DISTINCT " +
        "li.name regimen,sum(li.patientsontreatment) patientsontreatment, SUM(li.patientstoinitiatetreatment) " +
        "patientstoinitiatetreatment,  " +
        "SUM(li.patientsstoppedtreatment) patientsstoppedtreatment ");
    FROM(
      "regimen_line_items li  " +
        "JOIN requisitions r ON r.id = li.rnrid  " +
        "JOIN facilities f ON r.facilityid = f.id  " +
        "JOIN vw_districts d on d.district_id = f.geographicZoneId " +
        "JOIN regimens rg ON rg.code = li.code ");
    WHERE(programIsFilteredBy("r.programId"));
    WHERE(periodIsFilteredBy("r.periodId"));
    WHERE(userHasPermissionOnFacilityBy("r.facilityId"));
    WHERE(rnrStatusFilteredBy("r.status", filter.getAcceptedRnrStatuses()));

    if (filter.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("d"));
    }
    if (filter.getRegimenCategory() != 0) {
      WHERE(regimenIsFilteredBy("rg.categoryId"));
    }
    if (filter.getRegimen() != 0) {
      WHERE(regimenIsFilteredBy("li.regimenId"));
    }
    if (filter.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("f.typeId"));
    }
    if (filter.getFacility() != 0) {
      WHERE(facilityIsFilteredBy("r.facilityId"));
    }
    GROUP_BY("li.name");
    ORDER_BY("li.name ");
    return SQL();

  }

}
