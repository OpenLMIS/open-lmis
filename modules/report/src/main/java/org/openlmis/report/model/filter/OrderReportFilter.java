package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.util.Date;

/**
 * User: Wolde
 * Date: 8/5/13
 * Time: 6:32 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReportFilter implements ReportData {

    private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;
    private Date startDate;
    private Date endDate;
    private int quarterFrom;
    private int quarterTo;
    private int semiAnnualFrom;
    private int semiAnnualTo;

}
