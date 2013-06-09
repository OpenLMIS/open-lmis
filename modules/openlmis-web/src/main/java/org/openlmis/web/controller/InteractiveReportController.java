package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.Report;
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

/**

 */
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
       Integer userId = (Integer) request.getSession().getAttribute(USER_ID);

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

    @RequestMapping(value = "/reportdata/facilitylist", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_FACILITY_REPORT')")
    public Pages getFacilityLists( //@PathVariable(value = "reportKey") String reportKey,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                    @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                    HttpServletRequest request
                                    ) {

        Report report = reportManager.getReportByKey("facilities");//reportKey);
        List<FacilityReport> facilityReportList =  // (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteria(null);
        (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());


        return new Pages(page,totalRecCount,max,facilityReportList);
    }

    @RequestMapping(value = "/reportdata/mailingLabels", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_MAILING_LABEL_REPORT')")
    public Pages getFacilityListsWtihLables( //@PathVariable(value = "reportKey") String reportKey,
                      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                      @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                     HttpServletRequest request
    ) {

        Report report = reportManager.getReportByKey("mailinglabels");//reportKey);
        List<MailingLabelReport> mailingLabelReports =  // (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteria(null);

        (List<MailingLabelReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());

        return new Pages(page,totalRecCount,max,mailingLabelReports);
    }

    @RequestMapping(value = "/reportdata/consumption", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_CONSUMPTION_REPORT')")
    public Pages getConsumptionData( //@PathVariable(value = "reportKey") String reportKey,
                                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                             @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                             HttpServletRequest request

    ) {



        Report report = reportManager.getReportByKey("consumption");
        List<ConsumptionReport> consumptionReportList =
                (List<ConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());;

        return new Pages(page,totalRecCount,max,consumptionReportList);
    }

    @RequestMapping(value = "/reportdata/averageConsumption", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_AVERAGE_CONSUMPTION_REPORT')")
    public Pages getAverageConsumptionData( //@PathVariable(value = "reportKey") String reportKey,
                                     @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                     HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("average_consumption");
        List<AverageConsumptionReport> averageConsumptionReportList =
                (List<AverageConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());;

        return new Pages(page,totalRecCount,max,averageConsumptionReportList);
    }



    @RequestMapping(value = "/summary", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_SUMMARY_REPORT')")
    public Pages getSummaryData( //@PathVariable(value = "reportKey") String reportKey,
                                     @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                  //   @RequestParam(value = "period", required = false, defaultValue = "0") int period ,
                                  //   @RequestParam(value = "program", required = false, defaultValue = "0") int program ,
                                    HttpServletRequest request
    ) {



        Report report = reportManager.getReportByKey("summary");
        List<SummaryReport> reportList =
                (List<SummaryReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),null,page,max);
        int totalRecCount = 0;

        return new Pages(page,totalRecCount,max,reportList);
    }

    @RequestMapping(value = "/non_reporting", method = GET, headers = ACCEPT_JSON)
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

        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());

        return new Pages(page,totalRecCount,max,reportList);
    }


    @RequestMapping(value = "/stockedOut", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_STOCKED_OUT_REPORT')")
    public Pages getStockedOutReportData( //@PathVariable(value = "reportKey") String reportKey,
                                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                               // @RequestParam(value = "period", required = false, defaultValue = "0") int period ,
                                                HttpServletRequest request
    ) {



        Report report = reportManager.getReportByKey("stocked_out");
        List<MasterReport> reportList =
                (List<MasterReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);

        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());

        return new Pages(page,totalRecCount,max,reportList);
    }

     @RequestMapping(value = "/reportdata/adjustmentSummary", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_ADJUSTMENT_SUMMARY_REPORT')")
    public Pages getAdjustmentSummaryData(  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                            HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("adjustment_summary");
        List<AdjustmentSummaryReport> adjustmentSummaryReportList =
                (List<AdjustmentSummaryReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());;

        return new Pages(page,totalRecCount,max,adjustmentSummaryReportList);
    }

    @RequestMapping(value = "/reportdata/districtConsumption", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_DISTRICT_CONSUMPTION_REPORT')")
    public Pages getDistrictConsumptionData(  @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                            HttpServletRequest request

    ) {

        Report report = reportManager.getReportByKey("district_consumption");
        List<DistrictConsumptionReport> districtConsumptionReportList =
                (List<DistrictConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),request.getParameterMap(),page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(request.getParameterMap());

        return new Pages(page,totalRecCount,max,districtConsumptionReportList);
    }


}
