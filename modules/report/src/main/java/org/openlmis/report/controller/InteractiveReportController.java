/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.report.*;
import org.openlmis.report.response.OpenLmisResponse;
import org.openlmis.report.service.LabEquipmentStatusReportDataProvider;
import org.openlmis.report.service.UserSummaryReportProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class InteractiveReportController extends BaseController {

  @Autowired
  public ReportManager reportManager;


  @RequestMapping(value = "/reportdata/facilitylist", method = GET, headers = BaseController.ACCEPT_JSON)
  public Pages getFacilityLists(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request
  ) {

    Report report = reportManager.getReportByKey("facilities");
    List<FacilityReport> facilityReportList = (List<FacilityReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, facilityReportList);
  }

  @RequestMapping(value = "/reportdata/mailingLabels", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_MAILING_LABEL_REPORT')")
  public Pages getFacilityListsWtihLables( //@PathVariable(value = "reportKey") String reportKey,
                                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                           @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                           HttpServletRequest request
  ) {

    Report report = reportManager.getReportByKey("mailinglabels");
    List<MailingLabelReport> mailingLabelReports = (List<MailingLabelReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, mailingLabelReports);
  }

  @RequestMapping(value = "/reportdata/consumption", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_CONSUMPTION_REPORT')")
  public Pages getConsumptionData(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("consumption");
    List<ConsumptionReport> consumptionReportList =
      (List<ConsumptionReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, consumptionReportList);
  }

  @RequestMapping(value = "/reportdata/averageConsumption", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_AVERAGE_CONSUMPTION_REPORT')")
  public Pages getAverageConsumptionData(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request

  ) {

    Report report = reportManager.getReportByKey("average_consumption");
    List<AverageConsumptionReport> averageConsumptionReportList =
      (List<AverageConsumptionReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, averageConsumptionReportList);
  }


  @RequestMapping(value = "/reportdata/summary", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_SUMMARY_REPORT')")
  public Pages getSummaryData(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request
  ) {


    Report report = reportManager.getReportByKey("summary");
    List<SummaryReport> reportList =
      (List<SummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, reportList);
  }

  @RequestMapping(value = "/reportdata/non_reporting", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_NON_REPORTING_FACILITIES')")
  public Pages getNonReportingFacilitiesData( //@PathVariable(value = "reportKey") String reportKey,
                                              @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                              @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                              // @RequestParam(value = "period", required = false, defaultValue = "0") int period ,
                                              HttpServletRequest request
  ) {


    Report report = reportManager.getReportByKey("non_reporting");
    List<MasterReport> reportList =
      (List<MasterReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, reportList);
  }

  @RequestMapping(value = "/reportdata/adjustmentSummary", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ADJUSTMENT_SUMMARY_REPORT')")
  public Pages getAdjustmentSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                        @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                        HttpServletRequest request

  ) {

    Report report = reportManager.getReportByKey("adjustment_summary");
    List<AdjustmentSummaryReport> adjustmentSummaryReportList = (List<AdjustmentSummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, adjustmentSummaryReportList);
  }

  @RequestMapping(value = "/reportdata/districtConsumption", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_CONSUMPTION_REPORT')")
  public Pages getDistrictConsumptionData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                          @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                          HttpServletRequest request

  ) {

    Report report = reportManager.getReportByKey("district_consumption");
    List<DistrictConsumptionReport> districtConsumptionReportList =
      (List<DistrictConsumptionReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, districtConsumptionReportList);
  }

  @RequestMapping(value = "/reportdata/aggregateConsumption", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_CONSUMPTION_REPORT')")
  public Pages getAggregateConsumptionData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                          @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                          HttpServletRequest request

  ) {

    Report report = reportManager.getReportByKey("aggregate_consumption");
    List<DistrictConsumptionReport> districtConsumptionReportList =
        (List<DistrictConsumptionReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, districtConsumptionReportList);
  }

  @RequestMapping(value = "/reportdata/viewOrders", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_REPORT')")
  public Pages getOrderSummaryData(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("order_summary");
    List<OrderSummaryReport> orderReportList =
      (List<OrderSummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, orderReportList);
  }

  @RequestMapping(value = "/reportdata/supply_status", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_SUPPLY_STATUS_REPORT')")
  public Pages getSupplyStatusData(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("supply_status");
    List<SupplyStatusReport> supplyStatusReportList =
      (List<SupplyStatusReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, supplyStatusReportList);
  }

  @RequestMapping(value = "/reportdata/stockImbalance", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_STOCK_IMBALANCE_REPORT')")
  public Pages getStockImbalanceData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                     HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("stock_imbalance");
    List<StockImbalanceReport> stockImbalanceReportList =
      (List<StockImbalanceReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, stockImbalanceReportList);
  }


  //
  @RequestMapping(value = "/reportdata/stockedOut", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_STOCKED_OUT_REPORT')")
  public Pages getStockedOutData(
    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
    HttpServletRequest request
  ) {


    Report report = reportManager.getReportByKey("stocked_out");
    List<StockedOutReport> stockedOutReportList =
      (List<StockedOutReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, stockedOutReportList);
  }

  @RequestMapping(value = "/reportdata/rnr_feedback", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_RNR_FEEDBACK_REPORT')")
  public Pages getRnRFeedbackReportData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                        @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                        HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("rnr_feedback");
    List<RnRFeedbackReport> rnRFeedbackReports =
      (List<RnRFeedbackReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, rnRFeedbackReports);
  }


  @RequestMapping(value = "/reportdata/orderFillRate", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_FILL_RATE_REPORT')")
  public Pages getOrderFillRateData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                    HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("order_fill_rate");
    List<OrderFillRateReport> orderFillRateReportList =
      (List<OrderFillRateReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, orderFillRateReportList);
  }

  @RequestMapping(value = "/reportdata/regimenSummary", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REGIMEN_SUMMARY_REPORT')")
  public Pages getRegimenSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                     HttpServletRequest request

  ) {


    Report report = reportManager.getReportByKey("regimen_summary");
    List<RegimenSummaryReport> regimenSummaryReportList =
      (List<RegimenSummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, regimenSummaryReportList);
  }
    @RequestMapping(value = "/reportdata/aggregateregimenSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REGIMEN_SUMMARY_REPORT')")
    public Pages getAggregateRegimenSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                       HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("aggregate_regimen_summary");
        List<RegimenSummaryReport> regimenSummaryReportList =
                (List<RegimenSummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, regimenSummaryReportList);
    }


  @RequestMapping(value = "/reportdata/districtFinancialSummary", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT')")
  public Pages geDistrictFinancialSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                              @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                              HttpServletRequest request

  ) {
    Report report = reportManager.getReportByKey("district_financial_summary");
    List<DistrictSummaryReport> districtSummaryReportList =
      (List<DistrictSummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
    return new Pages(page, max, districtSummaryReportList);
  }


    @RequestMapping(value = "/reportdata/userSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_USER_SUMMARY_REPORT')")
    public Pages getUserSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("user_summary");
        List<UserSummaryReport> userSummmaryReportList =
                (List<UserSummaryReport>) report.getReportDataProvider().getMainReportData(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, userSummmaryReportList);
    }

    @RequestMapping(value = "/reportdata/userRoleAssignmentSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_USER_SUMMARY_REPORT')")
    public ResponseEntity<OpenLmisResponse> getUseRoleAssignmentSummary( HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("user_summary");

        UserSummaryReportProvider provider = (UserSummaryReportProvider) report.getReportDataProvider();
        return OpenLmisResponse.response("userAssignment",provider.getUserAssignments());
    }

    @RequestMapping(value = "/reportdata/labEquipmentList", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_LAB_EQUIPMENT_LIST_REPORT')")
    public Pages getLabEquipmentStatus(  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                         @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                         HttpServletRequest request) {

        Report report = reportManager.getReportByKey("lab_equipment_list");

        LabEquipmentStatusReportDataProvider provider = (LabEquipmentStatusReportDataProvider) report.getReportDataProvider();
        List<LabEquipmentStatusReport> labEquipmentStatusList = (List<LabEquipmentStatusReport>)
                provider.getMainReportData(request.getParameterMap(), request.getParameterMap(),page, max);

        return new Pages(page, max, labEquipmentStatusList);
    }
}
