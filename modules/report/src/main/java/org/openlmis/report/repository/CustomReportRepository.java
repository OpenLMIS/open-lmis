/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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

  public List<Map> getReportList(){
     return mapper.getListOfReports();
  }

  public List<Map> getReportData(Map filter){
    String reportKey = filter.get("report_key").toString();
    Map report = mapper.getCustomReportByKey(reportKey);
    String query = report.get("query").toString();
    filter.put("sql", query);
    return mapper.getReportData(filter);
  }

  public void insert(CustomReport report){
    mapper.insert(report);
  }
}
