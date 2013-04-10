package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.openlmis.core.domain.Facility;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.model.*;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.Pages;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.*;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportController  extends BaseController {

    public static final String USER_ID = "USER_ID";

    private ReportManager reportManager;
    @Autowired
    public ReportController(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @RequestMapping(value = "/download/{reportKey}/{outputOption}")
    public void showReport(
                                @PathVariable(value = "reportKey") String reportKey
                                , @PathVariable(value = "outputOption") String outputOption


                                , @RequestParam(value = "zoneId", required = false, defaultValue = "0") int zoneId
                                , @RequestParam(value = "facilityTypeId", required = false, defaultValue = "0") int facilityTypeId
                                , @RequestParam(value = "statusId", required = false, defaultValue = "" ) Boolean statusId

                                , ModelMap modelMap
                                , HttpServletRequest request
                                , HttpServletResponse response
                            )
    {
       Integer userId = (Integer) request.getSession().getAttribute(USER_ID);

        switch (outputOption.toUpperCase()){
            case "PDF":
                reportManager.showReport(userId, reportKey, null, ReportOutputOption.PDF, response);
                break;
            case "XLS":
                reportManager.showReport(userId, reportKey, null, ReportOutputOption.XLS, response);
        }

    }

    @RequestMapping(value = "/reportdata/facilitylist", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_FACILITY_REPORT')")
    public Pages getFacilityLists( //@PathVariable(value = "reportKey") String reportKey,
                                    @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                    @RequestParam(value = "max", required = false, defaultValue = "20") int max,
                                    @RequestParam(value = "zoneId", required = false, defaultValue = "0") int zoneId,
                                    @RequestParam(value = "facilityTypeId", required = false, defaultValue = "0") int facilityTypeId,
                                    @RequestParam(value = "statusId", required = false, defaultValue = "" ) Boolean statusId,
                                    @RequestParam(value = "code", required = false, defaultValue = "ASC") String code,
                                    @RequestParam(value = "facilityName", required = false, defaultValue = "") String facilityName,
                                    @RequestParam(value = "facilityType", required = false, defaultValue = "ASC") String facilityType
                                    ) {

        FacilityReportSorter facilityReportSorter = new FacilityReportSorter();
            facilityReportSorter.setFacilityName(facilityName);
            facilityReportSorter.setCode(code);
            facilityReportSorter.setFacilityType(facilityType);

        FacilityReportFilter facilityReportFilter = new FacilityReportFilter();
            facilityReportFilter.setZoneId(zoneId);
            facilityReportFilter.setFacilityTypeId(facilityTypeId);
            facilityReportFilter.setStatusId(statusId);

        Report report = reportManager.getReportByKey("facilities");//reportKey);
        List<FacilityReport> facilityReportList =  // (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteria(null);
        (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(facilityReportFilter,facilityReportSorter,page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(facilityReportFilter);
        //final int startIdx = (page - 1) * max;
        //final int endIdx = Math.min(startIdx + max, facilityReportList.size());
        //List<FacilityReport> facilityReportListJson =  (FacilityReport)facilityReportList;
        return new Pages(page,totalRecCount,max,facilityReportList);
    }

    @RequestMapping(value = "/reportdata/mailingLabels", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_MAILING_LABEL_REPORT')")
    public Pages getFacilityListsWtihLables( //@PathVariable(value = "reportKey") String reportKey,
                      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                      @RequestParam(value = "max", required = false, defaultValue = "20") int max,
                      @RequestParam(value = "facilityCodeFilter", required = false, defaultValue = "") String facilityCodeFilter,
                      @RequestParam(value = "facilityTypeId", required = false, defaultValue = "0") int facilityTypeId,
                      @RequestParam(value = "facilityNameFilter", required = false, defaultValue = "" ) String facilityNameFilter,
                      @RequestParam(value = "code", required = false, defaultValue = "ASC") String code,
                      @RequestParam(value = "facilityName", required = false, defaultValue = "") String facilityName,
                      @RequestParam(value = "facilityType", required = false, defaultValue = "ASC") String facilityType
    ) {

        MailingLabelReportSorter mailingLabelReportSorter = new MailingLabelReportSorter();
        mailingLabelReportSorter.setFacilityName(facilityName);
        mailingLabelReportSorter.setCode(code);
        mailingLabelReportSorter.setFacilityType(facilityType);

        MailingLabelReportFilter mailingLabelReportFilter = new MailingLabelReportFilter();
        mailingLabelReportFilter.setFacilityCode(facilityCodeFilter);
        mailingLabelReportFilter.setFacilityTypeId(facilityTypeId);
        mailingLabelReportFilter.setFacilityName(facilityNameFilter);

        Report report = reportManager.getReportByKey("mailinglabels");//reportKey);
        List<MailingLabelReport> mailingLabelReports =  // (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteria(null);
                (List<MailingLabelReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(mailingLabelReportFilter,mailingLabelReportSorter,page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(mailingLabelReportFilter);
        //final int startIdx = (page - 1) * max;
        //final int endIdx = Math.min(startIdx + max, facilityReportList.size());
        //List<FacilityReport> facilityReportListJson =  (FacilityReport)facilityReportList;
        return new Pages(page,totalRecCount,max,mailingLabelReports);
    }

    @RequestMapping(value = "/reportdata/consumption", method = GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'VIEW_CONSUMPTION_REPORT')")
    public Pages getConsumptionData( //@PathVariable(value = "reportKey") String reportKey,
                                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                             @RequestParam(value = "max", required = false, defaultValue = "20") int max,
                                             @RequestParam(value = "facilityCodeFilter", required = false, defaultValue = "0") String facilityCodeFilter,
                                             @RequestParam(value = "facilityTypeId", required = false, defaultValue = "0") int facilityTypeId,
                                             @RequestParam(value = "facilityNameFilter", required = false, defaultValue = "" ) String facilityNameFilter,
                                             @RequestParam(value = "code", required = false, defaultValue = "ASC") String code,
                                             @RequestParam(value = "facilityName", required = false, defaultValue = "") String facilityName,
                                             @RequestParam(value = "facilityType", required = false, defaultValue = "ASC") String facilityType
    ) {

        MailingLabelReportSorter mailingLabelReportSorter = new MailingLabelReportSorter();
        mailingLabelReportSorter.setFacilityName(facilityName);
        mailingLabelReportSorter.setCode(code);
        mailingLabelReportSorter.setFacilityType(facilityType);

        MailingLabelReportFilter mailingLabelReportFilter = new MailingLabelReportFilter();
        mailingLabelReportFilter.setFacilityCode(facilityCodeFilter);
        mailingLabelReportFilter.setFacilityTypeId(facilityTypeId);
        mailingLabelReportFilter.setFacilityName(facilityNameFilter);

        Report report = reportManager.getReportByKey("mailinglabels");//reportKey);
        List<FacilityReport> facilityReportList =  // (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteria(null);
                (List<FacilityReport>) report.getReportDataProvider().getReportDataByFilterCriteriaAndPagingAndSorting(mailingLabelReportFilter,mailingLabelReportSorter,page,max);
        int totalRecCount = report.getReportDataProvider().getReportDataCountByFilterCriteria(mailingLabelReportFilter);
        //final int startIdx = (page - 1) * max;
        //final int endIdx = Math.min(startIdx + max, facilityReportList.size());
        //List<FacilityReport> facilityReportListJson =  (FacilityReport)facilityReportList;
        return new Pages(page,totalRecCount,max,facilityReportList);
    }

}
