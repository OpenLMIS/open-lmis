/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionReportFilter implements ReportData {

    //top filters
    private int userId;

    // period selections
    //private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;

    private int facilityTypeId;
    private int zoneId;
    private int productId;
    private int facilityId;

    private Date startDate;
    private Date endDate;

}
