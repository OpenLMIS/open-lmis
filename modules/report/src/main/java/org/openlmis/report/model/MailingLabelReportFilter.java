package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 3/29/13
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailingLabelReportFilter implements ReportData {

    //top filters
    private String facilityCode;
    private String facilityName;
    private int facilityTypeId;


}


