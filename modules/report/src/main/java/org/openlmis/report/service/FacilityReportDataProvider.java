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

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.FacilityReportParam;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class FacilityReportDataProvider extends ReportDataProvider {

  @Autowired
  SelectedFilterHelper filterHelper;

  @Autowired
  private FacilityReportMapper facilityReportMapper;


  @Override
  protected List<? extends ReportData> getResultSet(Map<String, String[]> params) {
    return facilityReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(params), this.getUserId());
  }

  @Override
  public List<? extends ReportData> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    return facilityReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(filterCriteria), this.getUserId());
  }


  public FacilityReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    FacilityReportParam param = ParameterAdaptor.parse(filterCriteria, FacilityReportParam.class);
    return param;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return filterHelper.getProgramPeriodGeoZone(params);
  }
}
