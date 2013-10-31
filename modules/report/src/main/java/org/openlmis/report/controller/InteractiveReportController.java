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
import org.openlmis.report.*;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.report.*;
import org.openlmis.report.service.ReportLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class InteractiveReportController  extends BaseController {

    public static final String USER_ID = "USER_ID";

    @Autowired
    private ReportManager reportManager;

    @Autowired
    private ReportLookupService reportService;

    @RequestMapping(value = "/download/{reportKey}/{outputOption}")
    public void showReport(
            @PathVariable(value = "reportKey") String reportKey
            ,@PathVariable(value = "outputOption") String outputOption
            ,HttpServletRequest request
            ,HttpServletResponse response
    )
    {
       //TODO: change the methods to have a long user id parameter instead of integer
       Integer userId = Integer.parseInt( request.getSession().getAttribute(USER_ID).toString());

        switch (outputOption.toUpperCase()){
            case "PDF":
                reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.PDF, response);
                break;
            case "XLS":
                reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.XLS, response);
                break;
            case "HTML":
                reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.HTML, response);
        }

    }
   @RequestMapping(value = "/download/mailinglabels/list/{outputOption}")
    public void showMailingListReport(
            @PathVariable(value = "outputOption") String outputOption
            ,HttpServletRequest request
            ,HttpServletResponse response
    ){
       showReport("facility_mailing_list",outputOption,request,response);
   }

    @RequestMapping(value = "/reportdata/facilitylist", method = GET, headers = BaseController.ACCEPT_JSON)
    public Pages getFacilityLists(
                                    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                    HttpServletRequest request
                                    ) {

        Report report = reportManager.getReportByKey("facilities");
        List<FacilityReport> facilityReportList =  (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,facilityReportList);
    }

    @RequestMapping(value = "/reportdata/mailingLabels", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_MAILING_LABEL_REPORT')")
    public Pages getFacilityListsWtihLables( //@PathVariable(value = "reportKey") String reportKey,
                      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                      @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                     HttpServletRequest request
    ) {

        Report report = reportManager.getReportByKey("mailinglabels");
        List<MailingLabelReport> mailingLabelReports = (List<MailingLabelReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        return new Pages(page,max,mailingLabelReports);
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
                (List<ConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        return new Pages(page,max,consumptionReportList);
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
                (List<AverageConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,averageConsumptionReportList);
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
                (List<SummaryReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,reportList);
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
                (List<MasterReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,reportList);
    }

    @RequestMapping(value = "/reportdata/adjustmentSummary", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ADJUSTMENT_SUMMARY_REPORT')")
    public Pages getAdjustmentSummaryData(  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                            HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("adjustment_summary");
        List<AdjustmentSummaryReport> adjustmentSummaryReportList = (List<AdjustmentSummaryReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,adjustmentSummaryReportList);
    }

    @RequestMapping(value = "/reportdata/districtConsumption", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_CONSUMPTION_REPORT')")
    public Pages getDistrictConsumptionData(  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                            HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("district_consumption");
        List<DistrictConsumptionReport> districtConsumptionReportList =
                (List<DistrictConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,districtConsumptionReportList);
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
                (List<OrderSummaryReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,orderReportList);
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
        (List<SupplyStatusReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,supplyStatusReportList);
    }

    @RequestMapping(value = "/reportdata/stockImbalance", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_STOCK_IMBALANCE_REPORT')")
    public Pages getStockImbalanceData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                      @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                      HttpServletRequest request

    ) {



        Report report = reportManager.getReportByKey("stock_imbalance");
        List<StockImbalanceReport> stockImbalanceReportList =
                (List<StockImbalanceReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        return new Pages(page,max,stockImbalanceReportList);
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
                (List<StockedOutReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        return new Pages(page,max,stockedOutReportList);
    }

    @RequestMapping(value = "/reportdata/rnr_feedback", method = GET, headers = BaseController.ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_RNR_FEEDBACK_REPORT')")
    public Pages getRnRFeedbackReportData(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                       HttpServletRequest request

    ) {



        Report report = reportManager.getReportByKey("rnr_feedback");
        List<RnRFeedbackReport> rnRFeedbackReports =
                (List<RnRFeedbackReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        return new Pages(page,max,rnRFeedbackReports);
    }





}
