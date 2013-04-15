package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

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
public class FacilityReportFilter implements ReportData {

    //top filters
    private String facilityCodeId;
    private String facilityNameId;
    private int facilityTypeId;
    private int zoneId;
    private Boolean statusId;

}
