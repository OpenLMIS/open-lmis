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
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.RepairManagementReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.RepairManagementEquipmentListParam;
import org.openlmis.report.model.params.RepairManagementReportParam;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class RepairManagementEquipmentListDataProvider extends ReportDataProvider {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private RepairManagementReportMapper repairManagementReportMapper;

  private RepairManagementEquipmentListParam repairManagementEquipmentListParam = null;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {

    return repairManagementReportMapper.RepairManagementEquipmentList(getReportFilterData(params),this.getUserId());
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return repairManagementReportMapper.RepairManagementEquipmentList(getReportFilterData(filterCriteria), this.getUserId());
  }


  public RepairManagementEquipmentListParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      repairManagementEquipmentListParam = new RepairManagementEquipmentListParam();
      repairManagementEquipmentListParam.setZoneId(StringHelper.isBlank(filterCriteria,"zone") ? 0 : Integer.parseInt(filterCriteria.get("zone")[0]));  //defaults to 0
      repairManagementEquipmentListParam.setFacilityTypeId(filterCriteria.get("facilityType") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityType")[0])); //defaults to 0
      repairManagementEquipmentListParam.setAggregate(StringHelper.isBlank(filterCriteria, "aggregate") ? Boolean.FALSE : Boolean.valueOf(filterCriteria.get("aggregate")[0]));
      repairManagementEquipmentListParam.setProgramId(StringHelper.isBlank(filterCriteria, "program") ? 0 :Integer.parseInt(filterCriteria.get("program")[0]));
      repairManagementEquipmentListParam.setFacilityId(StringHelper.isBlank(filterCriteria, "facilityId") ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0]));
      repairManagementEquipmentListParam.setWorkingStatus(StringHelper.isBlank(filterCriteria, "workingStatus") ? null : filterCriteria.get("workingStatus")[0]);
    }
    return repairManagementEquipmentListParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }
}
