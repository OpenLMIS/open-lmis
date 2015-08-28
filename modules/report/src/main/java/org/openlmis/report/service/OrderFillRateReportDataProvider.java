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
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.model.report.OrderFillRateReport;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class OrderFillRateReportDataProvider extends ReportDataProvider {

    @Autowired
    private OrderFillRateReportMapper reportMapper;
    @Autowired
    private GeographicZoneRepository geographicZoneMapper;

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(filterCriteria, rowBounds, this.getUserId());
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

        List<MasterReport> reportList = new ArrayList<MasterReport>();
        MasterReport report = new MasterReport();
        report.details = reportMapper.getReport(filterCriteria, rowBounds, this.getUserId());
        List<OrderFillRateReport> summary = reportMapper.getReportSummary(filterCriteria, this.getUserId());
        String facility = (!StringHelper.isBlank(filterCriteria, "facility")) ? (filterCriteria.get("facility"))[0] : "";

        // TODO: move this to other section of the application
        OrderFillRateReport percentage = new OrderFillRateReport();
        percentage.setName("Order Fill Rate:");

        List<RequisitionGroup> facilityList = this.reportMapper.getFacility(Integer.parseInt(facility));

        //to be safe from a repetitive exception when ever facility id not selected
        if (facilityList != null && facilityList.size() > 0) {
            percentage.setNameLabel("Facility Name: ");
            percentage.setFacility(this.reportMapper.getFacility(Integer.parseInt(facility)).get(0).getName());
        }

        List<Integer> totalProductsReceivedList = reportMapper.getTotalProductsReceived(filterCriteria, this.getUserId());
        List<Integer> totalProductsOrderedList = reportMapper.getTotalProductsOrdered(filterCriteria, this.getUserId());

        if (totalProductsReceivedList.size() > 0 && totalProductsOrderedList.size() > 0) {
            String totalProductsReceived = totalProductsReceivedList.get(0).toString();
            String totalProductsOrdered = totalProductsOrderedList.get(0).toString();

            // Assume by default that the 100% of facilities didn't report
            Long percent = Long.parseLong("0");
            if (totalProductsOrdered != "0") {
                percent = Math.round((Double.parseDouble(totalProductsReceived) / Double.parseDouble(totalProductsOrdered)) * 100);
            }

            percentage.setCount(percent.toString() + "%");
        }

        summary.add(0, percentage);

        report.summary = summary;

        reportList.add(report);

        List<? extends ReportData> list;
        list = reportList;
        return list;
    }

    @Override
    public HashMap<String, String> getAdditionalReportData(Map params) {
        HashMap<String, String> result = new HashMap<String, String>();

        // spit out the summary section on the report.
        List<Integer> valueProductRecievedIntegerList = null;
        List<Integer> valueProductOrderedIntegerList = null;
        valueProductRecievedIntegerList = reportMapper.getTotalProductsReceived(params, this.getUserId());
        valueProductOrderedIntegerList = reportMapper.getTotalProductsOrdered(params, this.getUserId());
        String totalProductsReceived = (valueProductRecievedIntegerList == null ||
                valueProductRecievedIntegerList.size() <= 0 || valueProductRecievedIntegerList.get(0) == null) ? "0" :
                valueProductRecievedIntegerList.get(0).toString();
        String totalProductsOrdered = (valueProductOrderedIntegerList == null || valueProductOrderedIntegerList.size() <= 0 || valueProductOrderedIntegerList.get(0) == null
        ) ? "0" :
                valueProductOrderedIntegerList.get(0).toString();
        result.put("TOTAL_PRODUCTS_RECEIVED", totalProductsReceived);
        result.put("TOTAL_PRODUCTS_APPROVED", totalProductsOrdered);

        // Assume by default that the 100% of facilities didn't report
        Long percent = Long.parseLong("100");
        if (totalProductsOrdered != "0") {
            percent = Math.round((Double.parseDouble(totalProductsReceived) / Double.parseDouble(totalProductsOrdered)) * 100);
        }

        result.put("PERCENTAGE_ORDER_FILL_RATE", percent.toString());

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
        if (!schedule.equals("0") && !schedule.isEmpty() && !schedule.endsWith("undefined")) {
            header += "\nSchedule:" + this.reportMapper.getSchedule(Integer.parseInt(schedule)).get(0).getName();
        }
        ProcessingPeriod periodObject = this.reportMapper.getPeriodId(Integer.parseInt(period));

        if (period != "" && !period.endsWith("undefined")) {
            header += "\nPeriod : " + periodObject.getName() + " - " + periodObject.getStringYear();
        }
        if (!zone.equals("0") && !zone.isEmpty() && !zone.endsWith("undefined")) {
            header += "\nGeographic Zone: " + this.geographicZoneMapper.getById(Long.parseLong(zone)).getName();
        }
        if (!facilityType.isEmpty() && !facilityType.equals("0") && !facilityType.endsWith("undefined")) {
            header += "\nFacility Type : " + this.reportMapper.getFacilityType(Integer.parseInt(facilityType)).get(0).getName();
        } else {
            header += "\nFacility Type : All Facility Types";
        }

        if (!facility.isEmpty() && !facility.endsWith("undefined")) {
            List<RequisitionGroup> requisitionGroupList = null;
            requisitionGroupList = this.reportMapper.getFacility(Integer.parseInt(facility));
            if (requisitionGroupList != null && requisitionGroupList.size() > 0)
                header += "\nFacility Name: " + this.reportMapper.getFacility(Integer.parseInt(facility)).get(0).getName();
        }

        result.put("REPORT_FILTER_PARAM_VALUES", header.toString());
        return result;
    }
}
