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

import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.ConfigurationSettingService;

import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.RegimenService;
import org.openlmis.report.mapper.RegimenSummaryReportMapper;
import org.openlmis.report.model.ReportData;

import org.openlmis.report.model.ReportParameter;

import org.openlmis.report.model.params.RegimenSummaryReportParam;

import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@NoArgsConstructor
public class AggregateRegimenSummaryReportDataProvider extends ReportDataProvider {
    @Autowired
    private RegimenSummaryReportMapper reportMapper;
    @Autowired
    private ConfigurationSettingService configurationService;
    @Autowired
    private SelectedFilterHelper filterHelper;

    @Autowired
    public AggregateRegimenSummaryReportDataProvider(RegimenSummaryReportMapper mapper, ConfigurationSettingService configurationService) {
        this.reportMapper = mapper;
        this.configurationService = configurationService;
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        return reportMapper.getAggregateReport(getReportFilterData(filterCriteria), null, rowBounds, this.getUserId());
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getAggregateReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds, this.getUserId());
    }

    public ReportParameter getReportFilterData(Map<String, String[]> filterCriteria) {
        RegimenSummaryReportParam regimenSummaryReportParam = null;

        if (filterCriteria != null) {

            regimenSummaryReportParam = new RegimenSummaryReportParam();

            regimenSummaryReportParam.setRegimenCategoryId(StringHelper.isBlank(filterCriteria, "regimenCategory") ? 0 : Integer.parseInt(filterCriteria.get("regimenCategory")[0]));
            regimenSummaryReportParam.setRegimenId(StringHelper.isBlank(filterCriteria, "regimen") ? 0 : Integer.parseInt(filterCriteria.get("regimen")[0]));
            regimenSummaryReportParam.setPeriodId(Long.parseLong(filterCriteria.get("period")[0]));
            regimenSummaryReportParam.setZoneId(StringHelper.isBlank(filterCriteria, "zone") ? 0 : Integer.parseInt(filterCriteria.get("zone")[0]));
            regimenSummaryReportParam.setScheduleId(StringHelper.isBlank(filterCriteria, "schedule") ? 0 : Integer.parseInt(filterCriteria.get("schedule")[0]));
            regimenSummaryReportParam.setProgramId(StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]));
            regimenSummaryReportParam.setFacilityId(StringHelper.isBlank(filterCriteria, "facility") ? 0 : Integer.parseInt(filterCriteria.get("facility")[0]));
            regimenSummaryReportParam.setFacilityTypeId(StringHelper.isBlank(filterCriteria, ("facilityType")) ? 0 : Integer.parseInt(filterCriteria.get("facilityType")[0]));

        }
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
