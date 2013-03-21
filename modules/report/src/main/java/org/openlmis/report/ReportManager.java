package org.openlmis.report;

import lombok.*;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages both interactive web and jasper report integration
 */
@NoArgsConstructor
@AllArgsConstructor
public class ReportManager {

    private ReportAccessAuthorizer reportAccessAuthorizer;

    private List<Report> reports;

    private Map<String,Report> reportsByKey;

    private List<String> reportKeys;

    private Report report;

    public ReportManager(ReportAccessAuthorizer reportAccessAuthorizer, List<Report> reports) {

        this(reports);

        this.reportAccessAuthorizer = reportAccessAuthorizer;
    }

    private ReportManager(List<Report> reports){

        this.reports = reports;

        if(reports != null){

            reportsByKey = new HashMap<>();

            for (Report report: reports){
                 reportsByKey.put(report.getReportKey(),report);
            }

        }
    }

    /*
        Returns list of report keys of all registered Reports that are managed by ReportManager class.
        This report keys can be used for generating tree view(report navigation) on the web.
     */
    public List<String> getReportKeys() {
        return (List<String>) reportsByKey.keySet();
    }

    public Report getReportByKey(String reportKey){
        return reportsByKey.get(reportKey);
    }
}
