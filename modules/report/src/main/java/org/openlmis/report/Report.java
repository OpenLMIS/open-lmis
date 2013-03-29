package org.openlmis.report;

import lombok.Data;
import org.openlmis.report.dataprovider.ReportDataProvider;
import org.openlmis.report.model.ReportData;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 */
@Data
public class Report {

    public String reportKey;
    public String template;
    public ReportDataProvider reportDataProvider;
    public ReportData filterOption;
    /**
     *  report properties used by report design
     */
    public String title;
    public String name;
    public String id;
    public String version;
    /*                       */


}
