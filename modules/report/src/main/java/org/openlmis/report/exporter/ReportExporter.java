package org.openlmis.report.exporter;

import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.model.ReportData;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 *  Defines API for exporting reports.
 *  Implementation can be done for different Java reporting frameworks.(Jasper, BIRT)
 */
public interface ReportExporter {

    /**
     *
     * @param reportInputStream -   <b>The report being exported</b>
     * @param reportExtraParams  -  <b>Extra report parameters that can be passed to the report to fill report header and footer details</b>
     * @param reportData   - <b>DataSource used to fill the report</b>
     * @param outputOption  -   <b>Report out put option </b>
     * @param response - <b>HttpServletResponse for writing the report to OutputStream</b>
     */
    public void exportReport(InputStream reportInputStream,  HashMap<String, Object> reportExtraParams, List<? extends ReportData> reportData, ReportOutputOption outputOption, HttpServletResponse response);
}
