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

import java.text.DateFormat;
import java.util.Date;

/**
 * User: mahmed
 * Date: 6/26/13
 * Time: 3:58 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockedOutReportFilter implements ReportData {


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

    private int facilityTypeId;
    private String facilityType;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private String rgroup;
    private String facility;

    @Override
    public String toString(){

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Period : ").append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                append("Facility Types : ").append(this.getFacilityType()).append("\n").
                append("Reporting Groups : ").append(this.getRgroup());

        return filtersValue.toString();
    }
}
