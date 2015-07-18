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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.mapper.CCERepairManagementReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.CCERepairManagementReportParam;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;

@Component
@NoArgsConstructor
public class CCERepairManagementReportDataProvider extends ReportDataProvider {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  public ReportManager reportManager;

  @Autowired
  private CCERepairManagementReportMapper cceRepairManagementReportMapper;

  private CCERepairManagementReportParam cceRepairManagementReportParam = null;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {

    return cceRepairManagementReportMapper.SelectEquipmentCountByStatusEnergy(getReportFilterData(params),this.getUserId());
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

    return cceRepairManagementReportMapper.SelectEquipmentCountByStatusEnergy(getReportFilterData(filterCriteria),this.getUserId());
  }


  public CCERepairManagementReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      cceRepairManagementReportParam = new CCERepairManagementReportParam();
      cceRepairManagementReportParam.setAggregate(StringHelper.isBlank(filterCriteria, "aggregate") ? Boolean.FALSE : Boolean.valueOf(filterCriteria.get("aggregate")[0]));
      Long programId = StringHelper.isBlank(filterCriteria, "program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]);
      cceRepairManagementReportParam.setProgramId(programId);
      cceRepairManagementReportParam.setFacilityLevel(filterCriteria.get("facilityLevel")[0]);

      // List of facilities includes supervised and home facility
      List<Facility> facilities = facilityService.getUserSupervisedFacilities(this.getUserId(), programId, MANAGE_EQUIPMENT_INVENTORY);
      facilities.add(facilityService.getHomeFacility(this.getUserId()));

      StringBuilder str = new StringBuilder();
      for (Facility f : facilities) {
        str.append(f.getId());
        str.append(",");
      }
      if (str.length() > 1) {
        str.deleteCharAt(str.length()-1);
      }
      cceRepairManagementReportParam.setFacilityIds(str.toString());
      //Set template if aggregated
      Report report = reportManager.getReportByKey("cce_repair_management");
      if(cceRepairManagementReportParam.getAggregate()) {
        report.setTemplate("cce-aggregated-repair-management.jasper");
      }
      else{
        report.setTemplate("cce-repair-management.jasper");
      }


    }
    return cceRepairManagementReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }
}
