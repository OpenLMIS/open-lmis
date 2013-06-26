package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.util.Date;

/**
 * User: Wolde
 * Date: 6/26/13
 * Time: 3:58 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockedOutReportFilter implements ReportData {

    private Date startDate;
    private Date endDate;
    String period;
    String reportingGroup;
    String facilityType;
    String program;
    String schedule;
    String productCategory;
    String periodType;
}
