package org.openlmis.report.model.report;

import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.NameCount;

import java.util.List;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 5/2/13
 * Time: 3:05 PM
 */
public class NonReportingFacilityReport implements ReportData {

    public List<? extends ReportData> details;

    public List<? extends ReportData> summary;

}
