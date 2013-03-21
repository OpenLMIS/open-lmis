package org.openlmis.report.dataprovider;

import org.openlmis.report.model.ReportData;

import java.util.List;

/**
 */
public interface ReportDataProvider {

    public List<ReportData> getReportDataByFilterCriteria(ReportData filterCriteria);
}
