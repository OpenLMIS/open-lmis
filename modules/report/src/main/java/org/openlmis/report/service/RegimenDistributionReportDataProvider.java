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
import org.openlmis.report.mapper.RegimenSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.params.RegimenSummaryReportParam;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@NoArgsConstructor
public class RegimenDistributionReportDataProvider extends ReportDataProvider {

  @Autowired
  private RegimenSummaryReportMapper reportMapper;

  @Autowired
  private ConfigurationSettingService configurationService;

  @Value("${report.status.considered.accepted}")
  private String configuredAcceptedRnrStatuses;

  @Autowired
  private SelectedFilterHelper filterHelper;

  @Autowired
  public RegimenDistributionReportDataProvider(RegimenSummaryReportMapper mapper, ConfigurationSettingService configurationService) {
    this.reportMapper = mapper;
    this.configurationService = configurationService;
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getRegimenDistributionReport(getReportFilterData(filterCriteria), null, rowBounds, this.getUserId());
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getRegimenDistributionReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds, this.getUserId());
  }

  public ReportParameter getReportFilterData(Map<String, String[]> filterCriteria) {
    RegimenSummaryReportParam regimenSummaryReportParam = ParameterAdaptor.parse(filterCriteria, RegimenSummaryReportParam.class);
    regimenSummaryReportParam.setAcceptedRnrStatuses(configuredAcceptedRnrStatuses);
    return regimenSummaryReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    Map<String, String[]> modifiableParams = new HashMap<String, String[]>();
    modifiableParams.putAll(params);
    modifiableParams.put("userId", new String[]{String.valueOf(this.getUserId())});

    return filterHelper.getProgramPeriodGeoZone(modifiableParams);

  }
}
