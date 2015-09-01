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

package org.openlmis.report.repository;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.CustomReportMapper;
import org.openlmis.report.model.CustomReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class CustomReportRepository {

  @Autowired
  private CustomReportMapper mapper;

  public Map getQueryModelByKey(String key){
    return mapper.getCustomReportByKey(key);
  }

  public List<Map> getReportList() {
    return mapper.getListOfReports();
  }

  public List<CustomReport> getReportListWithFullAttributes() {
    return mapper.getListWithFullAttributes();
  }

  public List<Map> getReportData(Map filter) {
    String reportKey = filter.get("report_key").toString();
    Map report = mapper.getCustomReportByKey(reportKey);
    String query = report.get("query").toString();
    filter.put("sql", query);
    return mapper.getReportData(filter);
  }

  public void insert(CustomReport report) {
    mapper.insert(report);
  }

  public void update(CustomReport report) {
    mapper.update(report);
  }
}
