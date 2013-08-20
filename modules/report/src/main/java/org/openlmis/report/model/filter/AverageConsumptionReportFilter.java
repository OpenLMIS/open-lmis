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

import java.lang.String;
import java.text.DateFormat;
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
    private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;
    private int quarterFrom;
    private int quarterTo;
    private int semiAnnualFrom;
    private int semiAnnualTo;

    private int facilityTypeId;
    private String facilityType;
    private int zoneId;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private String rgroup;
    private int facilityId;
    private int programId;
    private int pdformat;

    private Date startDate;
    private Date endDate;

    @Override
    public String toString(){

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

        return "Period : "+  dateFormatter.format(this.getStartDate()) +" - "+ dateFormatter.format(this.getEndDate()) +" \n" +
                "Facility Types : "+ this.getFacilityType() +"\n " +
                "Reporting Groups : "+ this.getRgroup();

    }
}


