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
import org.openlmis.report.mapper.AggregateConsumptionReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.AggregateConsumptionReportParam;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class AggregateConsumptionReportDataProvider extends ReportDataProvider {


  @Value("${report.status.considered.accepted}")
  private String configuredAcceptedRnrStatuses;

  @Autowired
  private SelectedFilterHelper filterHelper;

  @Autowired
  private AggregateConsumptionReportMapper reportMapper;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    return getMainReportData(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getAggregateConsumptionReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds, this.getUserId());
  }

  public AggregateConsumptionReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    AggregateConsumptionReportParam param = ParameterAdaptor.parse(filterCriteria, AggregateConsumptionReportParam.class);
    param.setAcceptedRnrStatuses(configuredAcceptedRnrStatuses);
    return param;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {

      Map<String, String[]> modifiableParams = new HashMap<String, String[]>();
      modifiableParams.putAll(params);
      modifiableParams.put("userId", new String[]{String.valueOf(this.getUserId())});
      return filterHelper.getProgramPeriodGeoZone(modifiableParams);
  }

}
