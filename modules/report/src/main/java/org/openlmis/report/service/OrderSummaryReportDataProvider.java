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

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.order.service.OrderService;
import org.openlmis.report.mapper.OrderSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.OrderReportParam;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.report.util.Constants;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrStatus;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class OrderSummaryReportDataProvider extends ReportDataProvider {

  @Autowired
  private OrderSummaryReportMapper reportMapper;

  @Autowired
  private OrderService orderService;

  @Autowired
  private ConfigurationSettingService configurationService;

  @Autowired
  private ReportLookupService reportLookupService;

  @Autowired
  private RequisitionService requistionService;

  @Autowired
  private ProcessingPeriodService periodService;

  @Autowired
  private ProgramService programService;

  private OrderReportParam orderReportParam;


  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    return getMainReportData(filterCriteria, filterCriteria, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getOrderSummaryReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }

  public OrderReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

    if (filterCriteria != null) {
      orderReportParam = new OrderReportParam();

      if (filterCriteria.containsKey("orderId")) {
        orderReportParam.setOrderId(Long.parseLong(filterCriteria.get("orderId")[0]));

        orderReportParam.setFacility(reportLookupService.getFacilityNameForRnrId(orderReportParam.getOrderId()));
        orderReportParam.setPeriod(reportLookupService.getPeriodTextForRnrId(orderReportParam.getOrderId()));
        orderReportParam.setProgram(reportLookupService.getProgramNameForRnrId(orderReportParam.getOrderId()));
        orderReportParam.setProduct("All products");
      } else {

        orderReportParam.setFacilityTypeId(Strings.isNullOrEmpty(filterCriteria.get("facilityType")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
        orderReportParam.setFacilityId(Strings.isNullOrEmpty(filterCriteria.get("facilityId")[0]) ? 0L : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
        orderReportParam.setFacilityType((Strings.isNullOrEmpty(filterCriteria.get("facilityType")[0]) || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);

        orderReportParam.setScheduleId(Strings.isNullOrEmpty(filterCriteria.get("scheduleId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0])); //defaults to 0

        orderReportParam.setProductId(Strings.isNullOrEmpty(filterCriteria.get("product")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
        if (orderReportParam.getProductId() == 0) {
          orderReportParam.setProduct("All Products");
        } else if (orderReportParam.getProductId() == -1) {
          orderReportParam.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
        }
        orderReportParam.setOrderType(Strings.isNullOrEmpty(filterCriteria.get("orderType")[0]) ? "" : filterCriteria.get("orderType")[0]);
        orderReportParam.setPeriodId(Strings.isNullOrEmpty(filterCriteria.get("periodId")[0]) ? 0L : Integer.parseInt(filterCriteria.get("periodId")[0])); //defaults to 0
        orderReportParam.setPeriod(filterCriteria.get("period")[0]);
        orderReportParam.setProgramId(Strings.isNullOrEmpty(filterCriteria.get("programId")[0]) ? 0L : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
        orderReportParam.setProgram(filterCriteria.get("program")[0]);
        orderReportParam.setOrderId(reportMapper.getRequisitionId(orderReportParam.getFacilityId(), orderReportParam.getProgramId(), orderReportParam.getPeriodId()));
      }
      ;
      orderReportParam.setYear(
          new SimpleDateFormat("yyyy").format(  periodService.getById(
                                                                                  requistionService.getLWById(orderReportParam.getOrderId()).getPeriod().getId()).getStartDate()
          ));
    }
    return orderReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

  @Override
  public HashMap<String, String> getAdditionalReportData(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();
    result.put("ADDRESS", configurationService.getConfigurationStringValue("ORDER_REPORT_ADDRESS"));
    result.put("CUSTOM_REPORT_TITLE", configurationService.getConfigurationStringValue("ORDER_REPORT_TITLE"));
    result.put("ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER", configurationService.getConfigurationStringValue("ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER"));
    result.put("ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION", configurationService.getConfigurationStringValue("ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION"));

    Rnr rnr = requistionService.getLWById(orderReportParam.getOrderId());
    String orderNo = (orderService.getOrderNumberConfiguration().getOrderNumberFor(rnr.getId(), programService.getById(rnr.getProgram().getId()), rnr.isEmergency()));
    result.put("ORDER_NO", orderNo);

    List<RequisitionStatusChange> changes = reportMapper.getLastUsersWhoActedOnRnr(orderReportParam.getOrderId(), RnrStatus.AUTHORIZED.name());
    if(changes.size() > 0){

      result.put("AUTHORIZED_BY", changes.get(0).getCreatedBy().getFirstName() + " " + changes.get(0).getCreatedBy().getLastName() );
      result.put("AUTHORIZED_DATE", new SimpleDateFormat("dd/MM/yy hh:mm a").format(changes.get(0).getCreatedDate()) );
    }

    changes = reportMapper.getLastUsersWhoActedOnRnr(orderReportParam.getOrderId(), RnrStatus.IN_APPROVAL.name());
    if(changes.size() > 0){
      result.put("IN_APPROVAL_BY", changes.get(0).getCreatedBy().getFirstName() + " " + changes.get(0).getCreatedBy().getLastName()  );
      result.put("IN_APPROVAL_DATE", new SimpleDateFormat("dd/MM/yy hh:mm a").format(changes.get(0).getCreatedDate()) );
    }

    changes = reportMapper.getLastUsersWhoActedOnRnr(orderReportParam.getOrderId(), RnrStatus.APPROVED.name());
    if(changes.size() > 0){
      result.put("APPROVED_BY", changes.get(0).getCreatedBy().getFirstName() + " " + changes.get(0).getCreatedBy().getLastName()  );
      result.put("APPROVED_DATE", new SimpleDateFormat("dd/MM/yy hh:mm a").format(changes.get(0).getCreatedDate()) );
    }

    // register the facility related parameters here
    Facility currentFacility = reportLookupService.getFacilityForRnrId(orderReportParam.getOrderId());
    result.put("FACILITY_ADDRESS", currentFacility.getAddress1());
    result.put("PHONE", currentFacility.getMainPhone());
    result.put("FAX", currentFacility.getFax());
    result.put("DISTRICT", currentFacility.getGeographicZone().getName());
    if(currentFacility.getGeographicZone() != null) {
      result.put("REGION", currentFacility.getGeographicZone().getParent().getName());
    }
    return result;
  }
}
