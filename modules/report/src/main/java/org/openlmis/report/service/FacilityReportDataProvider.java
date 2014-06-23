/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.model.params.FacilityReportParam;
import org.openlmis.report.model.report.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.sorter.FacilityReportSorter;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class FacilityReportDataProvider extends ReportDataProvider {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private FacilityReportMapper facilityReportMapper;

  private FacilityReportParam facilityReportParam = null;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {

    return facilityReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(params));
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return facilityReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(filterCriteria));
  }


  public FacilityReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      facilityReportParam = new FacilityReportParam();
      facilityReportParam.setZoneId(StringHelper.isBlank(filterCriteria,"zone") ? 0 : Integer.parseInt(filterCriteria.get("zone")[0]));  //defaults to 0
      facilityReportParam.setFacilityTypeId(filterCriteria.get("facilityType") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityType")[0])); //defaults to 0
      facilityReportParam.setStatusId(StringHelper.isBlank(filterCriteria, "status") ? null : Boolean.valueOf(filterCriteria.get("status")[0]));
    }
    return facilityReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }
}
