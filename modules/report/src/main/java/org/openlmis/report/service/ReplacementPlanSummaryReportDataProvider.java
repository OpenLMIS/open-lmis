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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.ReplacementPlanSummaryMapper;
import org.openlmis.report.model.ReportData;

import org.openlmis.report.model.params.ReplacementPlanReportParam;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@NoArgsConstructor
@Service
public class ReplacementPlanSummaryReportDataProvider extends ReportDataProvider {

    @Autowired
    private ReplacementPlanSummaryMapper mapper;

    @Autowired
    private SelectedFilterHelper filterHelper;

    @Autowired
    private FacilityService facilityService;


    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {
        return getMainReportData(params, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    @Transactional
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sorter, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return mapper.getReport(geReportFilteredData(filterCriteria), rowBounds, this.getUserId());

    }

    public ReplacementPlanReportParam geReportFilteredData(Map<String, String[]> filterCriteria){

        ReplacementPlanReportParam planReportParam = new ReplacementPlanReportParam();
        Long programId = StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]);
        planReportParam.setProgramId(programId);
       // planReportParam.setFacilityLevel(filterCriteria.get("facilityLevel")[0]);
        String facilityLevel = StringHelper.isBlank(filterCriteria, "facilityLevel") ? null : ((String[]) filterCriteria.get("facilityLevel"))[0];
        planReportParam.setFacilityLevel(facilityLevel);

        String status = StringHelper.isBlank(filterCriteria, "status") ? null : ((String[]) filterCriteria.get("status"))[0];
        planReportParam.setStatus(status);
        Boolean disaggregated = StringHelper.isBlank(filterCriteria, "disaggregated") ? false : Boolean.parseBoolean(StringHelper.getValue(filterCriteria, "disaggregated"));
        planReportParam.setDisaggregated(disaggregated);

        // List of facilities includes supervised and home facility
        List<Facility> facilities = facilityService.getUserSupervisedFacilities(this.getUserId(), programId, MANAGE_EQUIPMENT_INVENTORY);
        facilities.add(facilityService.getHomeFacility(this.getUserId()));
        String facility = StringHelper.isBlank(filterCriteria, "facility") ? null : ((String[]) filterCriteria.get("facility"))[0];
        planReportParam.setFacility(facility);
        String plannedYear = StringHelper.isBlank(filterCriteria, "plannedYear") ? null : ((String[]) filterCriteria.get("plannedYear"))[0];
        planReportParam.setPlannedYear(plannedYear);

        StringBuilder str = new StringBuilder();
        str.append("{");
        for (Facility f : facilities) {
            str.append(f.getId());
            str.append(",");
        }
        if (str.length() > 1) {
            str.deleteCharAt(str.length()-1);
        }
        str.append("}");
        planReportParam.setFacilityIds(str.toString());

        return planReportParam;


    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramGeoZoneFacility(params);
    }


}


