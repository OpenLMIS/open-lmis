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
import org.openlmis.report.mapper.SeasonalRationingReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.SeasonalRationingReportParam;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class SeasonalRationingReportDataProvider  extends ReportDataProvider {

    @Autowired
    private SelectedFilterHelper filterHelper;

    @Autowired
    private SeasonalRationingReportMapper reportMapper;

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return getMainReportData(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getFilteredSortedPagedSeasonalRationingReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds, this.getUserId());
    }

    public SeasonalRationingReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

        SeasonalRationingReportParam seasonalRationingReportParam = new SeasonalRationingReportParam();

        seasonalRationingReportParam.setZoneId(StringHelper.isBlank(filterCriteria, "zone") ? 0 : Long.parseLong(filterCriteria.get("zone")[0]));
        seasonalRationingReportParam.setProductId(StringHelper.isBlank(filterCriteria, "product") ? 0 : Long.parseLong(filterCriteria.get("product")[0]));
        seasonalRationingReportParam.setProductCategoryId(StringHelper.isBlank(filterCriteria, "productCategory") ? 0L : Long.parseLong(filterCriteria.get("productCategory")[0]));
        seasonalRationingReportParam.setProgramId(StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]));

        return seasonalRationingReportParam;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramGeoZoneFacility(params);
    }

}
