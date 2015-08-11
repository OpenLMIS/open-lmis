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

import org.openlmis.report.DataSourceType;
import org.openlmis.report.model.ReportData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ReportDataProvider {

  private long userId;

  public void setUserId(Long id){
    userId = id;
  }

  public long getUserId(){
    return userId;
  }

  public final List<? extends ReportData> getReportDataByFilterCriteria(Map<String, String[]> params, DataSourceType dataSourceType){
    return getResultSetReportData(params);
  }

  public final List<? extends ReportData> getReportDataByFilterCriteria(Map<String, String[]> params){
      return getReportDataByFilterCriteria(params, DataSourceType.BEAN_COLLECTION_DATA_SOURCE);
  }

  protected abstract List<? extends ReportData> getResultSetReportData(Map<String, String[]> params);

  public abstract List<? extends ReportData> getMainReportData(Map<String, String[]> filter, Map<String, String[]> sorter, int page, int pageSize);

  public HashMap<String,String> getAdditionalReportData(Map params){
      return null;
  }

  public String getFilterSummary(Map<String, String[]> params){
    return "";
  }
}
