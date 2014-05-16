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

import org.openlmis.core.service.ConfigurationSettingService;

import org.openlmis.report.mapper.RegimenSummaryReportMapper;
import org.openlmis.report.model.ReportData;

import org.openlmis.report.model.ReportParameter;

import org.openlmis.report.model.params.RegimenSummaryReportParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@NoArgsConstructor
public class RegimenSummaryReportDataProvider extends ReportDataProvider {

    private RegimenSummaryReportMapper reportMapper;
    private ConfigurationSettingService configurationService;

    @Autowired
    public RegimenSummaryReportDataProvider(RegimenSummaryReportMapper mapper, ConfigurationSettingService configurationService) {
        this.reportMapper = mapper;
        this.configurationService = configurationService;
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    public ReportParameter getReportFilterData(Map<String, String[]> filterCriteria) {
        RegimenSummaryReportParam regimenSummaryReportParam = null;

        if (filterCriteria != null) {

            //regimenSummaryReportParam = new RegimenSummaryReportParam();
            //regimenSummaryReportParam.setRgroupId(StringUtils.isBlank(filterCriteria.get("requisitionGroup")[0]) ? 0 : Integer.parseInt(filterCriteria.get("requisitionGroup")[0])); //defaults to 0
            //regimenSummaryReportParam.setRegimenId(StringUtils.isBlank(filterCriteria.get("regimen")[0]) ? 0 : Integer.parseInt(filterCriteria.get("regimen")[0]));
            //regimenSummaryReportParam.setRegimenCategoryId(StringUtils.isBlank(filterCriteria.get("regimenCategory")[0]) ? 0 : Integer.parseInt(filterCriteria.get("regimenCategory")[0]));
        }
        return regimenSummaryReportParam;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return getReportFilterData(params).toString();
    }

}
