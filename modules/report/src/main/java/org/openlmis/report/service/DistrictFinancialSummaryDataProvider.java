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
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.DistrictFinancialSummaryMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.params.DistrictSummaryReportParam;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@NoArgsConstructor
public class DistrictFinancialSummaryDataProvider extends ReportDataProvider {

  private DistrictFinancialSummaryMapper reportMapper;
  private ConfigurationSettingService configurationService;

  @Autowired
  private SelectedFilterHelper filterHelper;

  @Autowired
  public DistrictFinancialSummaryDataProvider(DistrictFinancialSummaryMapper mapper, ConfigurationSettingService configurationService) {
    this.reportMapper = mapper;
    this.configurationService = configurationService;
  }

    @Override
    protected List<? extends ReportData> getResultSet(Map<String, String[]> filterCriteria) {
        return getReportBody(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

  @Override
  public List<? extends ReportData> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getReportPagedData(getReportFilterData(filterCriteria), sortCriteria, rowBounds,this.getUserId());
  }

  public ReportParameter getReportFilterData(Map<String, String[]> filterCriteria) {
      DistrictSummaryReportParam districtSummaryReportParam = null;

    if (filterCriteria != null) {
        districtSummaryReportParam = new DistrictSummaryReportParam();
        districtSummaryReportParam.setScheduleId(StringHelper.isBlank(filterCriteria, ("schedule")) ? 0 : Integer.parseInt(filterCriteria.get("schedule")[0]));
        districtSummaryReportParam.setPeriodId(Long.parseLong(filterCriteria.get("period")[0]));
        districtSummaryReportParam.setProgramId(StringHelper.isBlank(filterCriteria, ("program")) ? 0 : Integer.parseInt(filterCriteria.get("program")[0]));
        districtSummaryReportParam.setZoneId(StringHelper.isBlank(filterCriteria, ("zone")) ? 0 : Integer.parseInt(filterCriteria.get("zone")[0]));
    }
        return districtSummaryReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return filterHelper.getProgramPeriodGeoZone(params);
  }


}
