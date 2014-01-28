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
import org.openlmis.report.mapper.MailingLabelReportMapper;
import org.openlmis.report.model.params.MailingLabelReportParam;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.sorter.MailingLabelReportSorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class MailingLabelReportDataProvider extends ReportDataProvider {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private MailingLabelReportMapper mailingLabelReportMapper;

  private MailingLabelReportParam mailingLabelReportParam = null;

  private ReportData getMailingLabelReport(Facility facility) {
    if (facility == null) return null;
    return new MailingLabelReport();
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {

    return getMainReportData(params, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

  }

  private List<ReportData> getListMailingLabelsReport(List<Facility> facilityList) {

    if (facilityList == null) return null;

    List<ReportData> facilityReportList = new ArrayList<>(facilityList.size());

    for (Facility facility : facilityList) {
      facilityReportList.add(getMailingLabelReport(facility));
    }

    return facilityReportList;
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sorterCriteria, int page, int pageSize) {

    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

    MailingLabelReportSorter mailingLabelReportSorter = null;

    if (sorterCriteria != null) {
      mailingLabelReportSorter = new MailingLabelReportSorter();
      mailingLabelReportSorter.setFacilityName(sorterCriteria.get("facilityName") == null ? "" : sorterCriteria.get("facilityName")[0]);
      mailingLabelReportSorter.setCode(sorterCriteria.get("code") == null ? "" : sorterCriteria.get("code")[0]);
      mailingLabelReportSorter.setFacilityType(sorterCriteria.get("facilityType") == null ? "ASC" : sorterCriteria.get("facilityType")[0]);
    }
    return mailingLabelReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(filterCriteria), mailingLabelReportSorter, rowBounds);
  }

  public MailingLabelReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      mailingLabelReportParam = new MailingLabelReportParam();
      mailingLabelReportParam.setFacilityTypeId((filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])));
      mailingLabelReportParam.setRgroupId((filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])));
    }
    return mailingLabelReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

}
