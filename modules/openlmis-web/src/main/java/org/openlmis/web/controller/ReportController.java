package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.dto.Product;
import org.openlmis.report.model.report.FacilityReport;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.report.ConsumptionReport;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.report.SummaryReport;
import org.openlmis.report.service.ProductReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

/**

 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportController  extends BaseController {

    public static final String USER_ID = "USER_ID";

    private ReportManager reportManager;
    private ProductReportService productReportService;

    @Autowired
    public ReportController(ReportManager reportManager, ProductReportService productReportService) {
        this.reportManager  = reportManager;
        this.productReportService = productReportService;
    }

    //TODO: take this out to an appropriate class
    @RequestMapping(value="/products", method = GET, headers = ACCEPT_JSON)
    public List<Product> getProducts(){
          return this.productReportService.getAllProducts();
    }

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
                                             @RequestParam(value = "facilityCodeFilter", required = false, defaultValue = "0") String facilityCodeFilter,
                                             @RequestParam(value = "facilityTypeId", required = false, defaultValue = "0") int facilityTypeId,
                                             @RequestParam(value = "facilityNameFilter", required = false, defaultValue = "" ) String facilityNameFilter,
                                             @RequestParam(value = "code", required = false, defaultValue = "ASC") String code,
                                             @RequestParam(value = "facilityName", required = false, defaultValue = "") String facilityName,
                                             @RequestParam(value = "facilityType", required = false, defaultValue = "ASC") String facilityType
    ) {



        Report report = reportManager.getReportByKey("consumption");
        List<ConsumptionReport> facilityReportList =
                (List<ConsumptionReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(null,null,page,max);
        int totalRecCount = 0;

        return new Pages(page,totalRecCount,max,facilityReportList);
    }


    @RequestMapping(value = "/summary", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_SUMMARY_REPORT')")
    public Pages getConsumptionData( //@PathVariable(value = "reportKey") String reportKey,
                                     @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(value = "max", required = false, defaultValue = "10") int max,
                                     @RequestParam(value = "period", required = false, defaultValue = "0") int period ,
                                    HttpServletRequest request
    ) {



        Report report = reportManager.getReportByKey("summary");
        List<SummaryReport> reportList =
                (List<SummaryReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(request.getParameterMap(),null,page,max);
        int totalRecCount = 0;

        return new Pages(page,totalRecCount,max,reportList);
    }

}
