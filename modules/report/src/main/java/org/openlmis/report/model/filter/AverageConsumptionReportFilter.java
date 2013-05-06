package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 5/2/13
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageConsumptionReportFilter  implements ReportData {

    //top filters
    private int userId;

    // period selections
    //private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;

    private int facilityTypeId;
    private String facilityType;
    private int zoneId;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private int facilityId;

    private Date startDate;
    private Date endDate;
}


