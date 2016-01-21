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

package org.openlmis.report.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.report.*;
import org.openlmis.report.model.report.vaccine.ReplacementPlanSummary;
import org.openlmis.report.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<FacilityReport> facilityReportList = (List<FacilityReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<MailingLabelReport> mailingLabelReports = (List<MailingLabelReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<ConsumptionReport> consumptionReportList =
                (List<ConsumptionReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, consumptionReportList);
    }
    
    @RequestMapping(value = "/reportdata/summary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_SUMMARY_REPORT')")
    public Pages getSummaryData(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
            HttpServletRequest request
    ) {


        Report report = reportManager.getReportByKey("summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<SummaryReport> reportList =
                (List<SummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<MasterReport> reportList =
                (List<MasterReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, reportList);
    }

    @RequestMapping(value = "/reportdata/adjustmentSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ADJUSTMENT_SUMMARY_REPORT')")
    public Pages getAdjustmentSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                          @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                          HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("adjustment_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<AdjustmentSummaryReport> adjustmentSummaryReportList = (List<AdjustmentSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, adjustmentSummaryReportList);
    }

    @RequestMapping(value = "/reportdata/districtConsumption", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_CONSUMPTION_REPORT')")
    public Pages getDistrictConsumptionData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                            HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("district_consumption");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<DistrictConsumptionReport> districtConsumptionReportList =
                (List<DistrictConsumptionReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, districtConsumptionReportList);
    }

    @RequestMapping(value = "/reportdata/aggregateConsumption", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_CONSUMPTION_REPORT')")
    public Pages getAggregateConsumptionData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                             @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                             HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("aggregate_consumption");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<DistrictConsumptionReport> districtConsumptionReportList =
                (List<DistrictConsumptionReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<OrderSummaryReport> orderReportList =
                (List<OrderSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<SupplyStatusReport> supplyStatusReportList =
                (List<SupplyStatusReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, supplyStatusReportList);
    }

    @RequestMapping(value = "/reportdata/stockImbalance", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_STOCK_IMBALANCE_REPORT')")
    public Pages getStockImbalanceData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                       HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("stock_imbalance");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<StockImbalanceReport> stockImbalanceReportList =
                (List<StockImbalanceReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
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
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<StockedOutReport> stockedOutReportList =
                (List<StockedOutReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, stockedOutReportList);
    }

    @RequestMapping(value = "/reportdata/rnr_feedback", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_RNR_FEEDBACK_REPORT')")
    public Pages getRnRFeedbackReportData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                          @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                          HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("rnr_feedback");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<RnRFeedbackReport> rnRFeedbackReports =
                (List<RnRFeedbackReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, rnRFeedbackReports);
    }


    @RequestMapping(value = "/reportdata/orderFillRate", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_FILL_RATE_REPORT')")
    public Pages getOrderFillRateData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                      @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                      HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("order_fill_rate");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<MasterReport> orderFillRateReportList =
                (List<MasterReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, orderFillRateReportList);
    }

    @RequestMapping(value = "/reportdata/regimenSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REGIMEN_SUMMARY_REPORT')")
    public Pages getRegimenSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                       HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("regimen_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<RegimenSummaryReport> regimenSummaryReportList =
                (List<RegimenSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, regimenSummaryReportList);
    }

    @RequestMapping(value = "/reportdata/aggregateRegimenSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REGIMEN_SUMMARY_REPORT')")
    public Pages getAggregateRegimenSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("aggregate_regimen_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<RegimenSummaryReport> regimenSummaryReportList =
                (List<RegimenSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, regimenSummaryReportList);
    }

    @RequestMapping(value = "/reportdata/getRegimenDistribution", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_REGIMEN_SUMMARY_REPORT')")
    public Pages getRegimenDistributionData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                            HttpServletRequest request

    ) {


        Report report = reportManager.getReportByKey("regimen_distribution");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<RegimenSummaryReport> regimenSummaryReportList =
                (List<RegimenSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, regimenSummaryReportList);
    }

    @RequestMapping(value = "/reportdata/districtFinancialSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT')")
    public Pages geDistrictFinancialSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("district_financial_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<DistrictSummaryReport> districtSummaryReportList =
                (List<DistrictSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, districtSummaryReportList);
    }


    @RequestMapping(value = "/reportdata/userSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_USER_SUMMARY_REPORT')")
    public Pages getUserSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                    HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("user_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<UserSummaryReport> userSummmaryReportList =
                (List<UserSummaryReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, userSummmaryReportList);
    }

    @RequestMapping(value = "/reportdata/userRoleAssignmentSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_USER_SUMMARY_REPORT')")
    public ResponseEntity<OpenLmisResponse> getUseRoleAssignmentSummary(HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("user_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        UserSummaryReportProvider provider = (UserSummaryReportProvider) report.getReportDataProvider();
        return OpenLmisResponse.response("userAssignment", provider.getUserAssignments());
    }


    @RequestMapping(value = "/reportdata/pipelineExport", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_PIPELINE_EXPORT')")
    public Pages getPipelineExportData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                       HttpServletRequest request) {

        Report report = reportManager.getReportByKey("pipeline_export");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        PipelineExportReportProvider provider = (PipelineExportReportProvider) report.getReportDataProvider();
        List<PipelineExportReport> pipelineExportData = (List<PipelineExportReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, pipelineExportData);
    }


    @RequestMapping(value = "/reportdata/labEquipmentList", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_LAB_EQUIPMENT_LIST_REPORT')")
    public Pages getLabEquipmentStatus(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                       HttpServletRequest request) {

        Report report = reportManager.getReportByKey("lab_equipment_list");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        LabEquipmentStatusReportDataProvider provider = (LabEquipmentStatusReportDataProvider) report.getReportDataProvider();
        List<LabEquipmentStatusReport> labEquipmentStatusList = (List<LabEquipmentStatusReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, labEquipmentStatusList);
    }

  @RequestMapping(value = "/reportdata/cceStorageCapacity", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_CCE_STORAGE_CAPACITY_REPORT')")
  public Pages getCCEStorageCapacity(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                     HttpServletRequest request) {
    Report report = reportManager.getReportByKey("cce_storage_capacity");
    report.getReportDataProvider().setUserId(loggedInUserId(request));
    CCEStorageCapacityReportDataProvider provider = (CCEStorageCapacityReportDataProvider)report.getReportDataProvider();
    List<CCEStorageCapacityReport> list = (List<CCEStorageCapacityReport>)
        provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

    return new Pages(page, max, list);
  }

  @RequestMapping(value = "/reportdata/labEquipmentsByFundingSource", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_LAB_EQUIPMENTS_BY_FUNDING_SOURCE')")
    public Pages getLabEquipmentListByFundingSource(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                    HttpServletRequest request) {

        Report report = reportManager.getReportByKey("lab_equipments_by_donor");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        LabEquipmentsByDonorReportDataProvider provider = (LabEquipmentsByDonorReportDataProvider) report.getReportDataProvider();
        List<LabEquipmentsByDonorReport> labEquipmentsListByDonor = (List<LabEquipmentsByDonorReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, labEquipmentsListByDonor);
    }

    @RequestMapping(value = "/reportdata/pushedProductList", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_FILL_RATE_REPORT')")
    public Pages getPushedProductsReport(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                         @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                         HttpServletRequest request) {

        Report report = reportManager.getReportByKey("pushed_product_list");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        PushedProductReportDataProvider provider = (PushedProductReportDataProvider) report.getReportDataProvider();
        List<OrderFillRateReport> pushedProductList = (List<OrderFillRateReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, pushedProductList);
    }

    @RequestMapping(value = "/reportdata/orderFillRateReportSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ORDER_FILL_RATE_SUMMARY_REPORT')")
    public Pages getOrderFillRateReportSummaryData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                   @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                   HttpServletRequest request) {

        Report report = reportManager.getReportByKey("order_fill_rate_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        OrderFillRateReportSummaryDataProvider provider = (OrderFillRateReportSummaryDataProvider) report.getReportDataProvider();
        List<OrderFillRateSummaryReport> orderFillRateReportList = (List<OrderFillRateSummaryReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, orderFillRateReportList);
    }

    @RequestMapping(value = "/reportdata/functioningLabEquipment", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_LAB_EQUIPMENTS_BY_LOCATION_REPORT')")
    public Pages getFunctioningLabEquipmentWithServiceContract(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                               @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                               HttpServletRequest request) {

        Report report = reportManager.getReportByKey("lab_equipments_functioning");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        EquipmentsFunctioningDataProvider provider = (EquipmentsFunctioningDataProvider) report.getReportDataProvider();
        List<LabEquipmentStatusReport> labEquipmentFunctioningList = (List<LabEquipmentStatusReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, labEquipmentFunctioningList);
    }

    @RequestMapping(value = "/reportdata/nonFunctioningLabEquipment", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_LAB_EQUIPMENTS_BY_LOCATION_REPORT')")
    public Pages getNonFunctioningLabEquipmentWithServiceContract(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                                  @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                                  HttpServletRequest request) {

        Report report = reportManager.getReportByKey("lab_equipments_non_functioning");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        EquipmentsNonFunctioningDataProvider provider = (EquipmentsNonFunctioningDataProvider) report.getReportDataProvider();
        List<LabEquipmentStatusReport> labEquipmentNonFunctioningList = (List<LabEquipmentStatusReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, labEquipmentNonFunctioningList);
    }


    @RequestMapping(value = "/reportdata/seasonalityRationing", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_SEASONALITY_RATIONING_REPORT')")
    public Pages getSeasonalityRationingAdjustmentReport(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                         @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                         HttpServletRequest request) {

        Report report = reportManager.getReportByKey("seasonality_rationing");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        SeasonalRationingReportDataProvider provider = (SeasonalRationingReportDataProvider) report.getReportDataProvider();
        List<SeasonalRationingReport> seasonalRationingAdjustmentList = (List<SeasonalRationingReport>)
                provider.getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, seasonalRationingAdjustmentList);
    }

    @RequestMapping(value = "/reportdata/timeliness", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_TIMELINESS_REPORT')")
    public Pages getTimelinessData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                   @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                   HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("timeliness");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<TimelinessReport> timelinessReportList =
                (List<TimelinessReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, timelinessReportList);
    }
    @RequestMapping(value = "/reportdata/cceRepairManagement", method = GET, headers = BaseController.ACCEPT_JSON)
    public Pages getRepairManagementSummary(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
            HttpServletRequest request
    ) {

        Report report = reportManager.getReportByKey("cce_repair_management");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<CCERepairManagementReport> CCERepairManagementReport = (List<CCERepairManagementReport>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, CCERepairManagementReport);
    }


    @RequestMapping(value = "/reportdata/cceRepairManagementEquipmentList", method = GET, headers = BaseController.ACCEPT_JSON)
    public Pages getRepairManagementEquipmentList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
            HttpServletRequest request
    ) {

        Report report = reportManager.getReportByKey("cce_repair_management_equipment_list");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<CCERepairManagementEquipmentList> repairManagementEquipmentList = (List<CCERepairManagementEquipmentList>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, repairManagementEquipmentList);
    }


    @RequestMapping(value = "/reportdata/replacementPlanSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY')")
    public Pages getReplacementPlanSummaryReport(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                 @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                 HttpServletRequest request) {

        Report report = reportManager.getReportByKey("replacement_plan_summary");
        report.getReportDataProvider().setUserId(loggedInUserId(request));

        List<ReplacementPlanSummary> reportList =
                (List<ReplacementPlanSummary>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, reportList);
    }


    @RequestMapping(value = "/reportdata/equipmentsInNeedForReplacement", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY')")
    public Pages getReplacementToBeReplaced(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                 @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                                 HttpServletRequest request) {

        Report report = reportManager.getReportByKey("equipment_replacement_list");
        report.getReportDataProvider().setUserId(loggedInUserId(request));

        List<ReplacementPlanSummary> SummaryList = (List<ReplacementPlanSummary>)report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);

        return new Pages(page, max, SummaryList);
    }

    @RequestMapping(value = "/reportdata/coldChainEquipment", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT')")
    public Pages getColdChainEquipment(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                   @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                   HttpServletRequest request

    ) {
        Report report = reportManager.getReportByKey("cold_chain_equipment");
        report.getReportDataProvider().setUserId(loggedInUserId(request));
        List<CCEInventoryReportDatum> reportData =
                (List<CCEInventoryReportDatum>) report.getReportDataProvider().getReportBody(request.getParameterMap(), request.getParameterMap(), page, max);
        return new Pages(page, max, reportData);
    }

}
