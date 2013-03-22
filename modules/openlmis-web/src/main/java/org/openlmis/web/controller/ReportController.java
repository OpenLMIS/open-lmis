package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.openlmis.report.Report;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**

 */
@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportController {

    private ReportManager reportManager;
    @Autowired
    public ReportController(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @RequestMapping(value = "/{reportKey}/{outputOption}")
    public String showReport(@PathVariable(value = "reportKey") String reportKey, @PathVariable(value = "outputOption") String outputOption,ModelMap modelMap){
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        Report report = reportManager.getReportByKey(reportKey);
       List<ReportData> facilityReportList = report.getReportDataProvider().getReportDataByFilterCriteria(null);
        modelMap.addAttribute("datasource", new JRBeanCollectionDataSource(facilityReportList));
        modelMap.addAttribute("format",outputOption);

        return "facilities";
    }

}
