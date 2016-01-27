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
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.OrderFillRateReportParam;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.model.report.OrderFillRateReport;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
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
  @Autowired
  private SelectedFilterHelper selectedFilterHelper;


  @Override
  protected List<? extends ReportData> getResultSet(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(ParameterAdaptor.parse(filterCriteria, OrderFillRateReportParam.class), rowBounds, this.getUserId());
  }

  @Override
  public List<? extends ReportData> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

    OrderFillRateReportParam parameter = ParameterAdaptor.parse(filterCriteria, OrderFillRateReportParam.class);
    List<MasterReport> reportList = new ArrayList<MasterReport>();
    MasterReport report = new MasterReport();
    report.setDetails(reportMapper.getReport(parameter, rowBounds, this.getUserId()));
    List<OrderFillRateReport> summary = reportMapper.getReportSummary(parameter, this.getUserId());
    OrderFillRateReport percentage = new OrderFillRateReport();
    percentage.setName("Order Fill Rate:");

    List<Integer> totalProductsReceivedList = reportMapper.getTotalProductsReceived(parameter, this.getUserId());
    List<Integer> totalProductsOrderedList = reportMapper.getTotalProductsOrdered(parameter, this.getUserId());

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

    report.setSummary(summary);

    reportList.add(report);

    List<? extends ReportData> list;
    list = reportList;
    return list;
  }

  @Override
  public HashMap<String, String> getExtendedHeader(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();
    OrderFillRateReportParam parameter = ParameterAdaptor.parse(params, OrderFillRateReportParam.class);
    // spit out the summary section on the report.
    List<Integer> valueProductRecievedIntegerList = null;
    List<Integer> valueProductOrderedIntegerList = null;
    valueProductRecievedIntegerList = reportMapper.getTotalProductsReceived(parameter, this.getUserId());
    valueProductOrderedIntegerList = reportMapper.getTotalProductsOrdered(parameter, this.getUserId());
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
    result.put("REPORT_FILTER_PARAM_VALUES", selectedFilterHelper.getProgramGeoZoneFacility(params));
    return result;
  }
}
