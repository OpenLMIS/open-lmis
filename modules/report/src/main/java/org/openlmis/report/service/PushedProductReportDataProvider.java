/*
        ~ This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
        ~
        ~ This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
        ~
        ~ This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
        ~
        ~ You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
*/
package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class PushedProductReportDataProvider extends ReportDataProvider {
    @Autowired
    private OrderFillRateReportMapper reportMapper;
    @Autowired
    private GeographicZoneRepository geographicZoneMapper;

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {
        return getMainReportData(params, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sorter, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getPushedProducts(filterCriteria, rowBounds, this.getUserId());
    }

    @Override
    public HashMap<String, String> getAdditionalReportData(Map params) {
        HashMap<String, String> result = new HashMap<String, String>();

        // Interprate the different reporting parameters that were selected on the UI

        String period = (StringHelper.isBlank(params, "period")) ? "" : ((String[]) params.get("period"))[0];
        String facility = (StringHelper.isBlank(params, "facility")) ? "" : ((String[]) params.get("facility"))[0];
        String facilityType = (StringHelper.isBlank(params, "facilityType")) ? "" : ((String[]) params.get("facilityType"))[0];
        String program = (StringHelper.isBlank(params, "program")) ? "" : ((String[]) params.get("program"))[0];
        String zone = (StringHelper.isBlank(params, "zone")) ? "" : ((String[]) params.get("zone"))[0];
        String schedule = (StringHelper.isBlank(params, "schedule")) ? "" : ((String[]) params.get("schedule"))[0];

        // compose the filter text as would be presented on the pdf reports.
        String header = "";
        if (!program.equals("0") && !program.isEmpty() && !program.endsWith("undefined")) {
            header += "Program: " + this.reportMapper.getProgram(Integer.parseInt(program)).get(0).getName();
        }
        if(!schedule.equals("0") && !schedule.isEmpty() && !schedule.endsWith("undefined")){
            header += "\nSchedule:" + this.reportMapper.getSchedule(Integer.parseInt(schedule)).get(0).getName();
        }
        ProcessingPeriod periodObject = this.reportMapper.getPeriodId(Integer.parseInt(period));

        if (period != "" && !period.endsWith("undefined")) {
            header += "\nPeriod : " + periodObject.getName() + " - " + periodObject.getStringYear();
        }
        if (!zone.equals("0") && !zone.isEmpty() && !zone.endsWith("undefined")) {
            header += "\nGeographic Zone: " + this.geographicZoneMapper.getById(Integer.parseInt(zone)).getName();
        }else{
            header += "\nGeographic Zone : All Geographic Zones";

        }
        if (!facilityType.isEmpty() && !facilityType.equals("0") && !facilityType.endsWith("undefined")) {
            header += "\nFacility Type : " + this.reportMapper.getFacilityType(Integer.parseInt(facilityType)).get(0).getName();
        } else {
            header += "\nFacility Type : All Facility Types";
        }

        if (facility != "" && !facility.endsWith("undefined")) {
            header += "\nFacility Name: " + this.reportMapper.getFacility(Integer.parseInt(facility)).get(0).getName();
        }

        result.put("REPORT_FILTER_PARAM_VALUES", header.toString());
        return result;
    }
}
