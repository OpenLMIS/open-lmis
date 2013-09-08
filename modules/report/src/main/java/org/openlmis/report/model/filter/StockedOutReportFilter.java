/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
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


    private int programId;
    private String program;
    private int rgroupId;
    private String rgroup;
    private int productCategoryId;
    private String productCategory;
    private int productId;
    private String product;
    private int facilityTypeId;
    private String facilityType;
    private int facilityId;
    private String facility;
    private int zoneId;
    private String zone;

    @Override
    public String toString(){

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

        StringBuilder filtersValue = new StringBuilder("");

        filtersValue.
                append("Period : ").append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                append("Program : ").append(this.getProgram()).append("\n").
                append("Requisition Group : ").append(this.getRgroup()).append("\n").
                append("Product Category : ").append(this.getProductCategory()).append("\n").
                append("Product : ").append(this.getProduct()).append("\n").
                append("Zone : ").append(this.getZone()).append("\n").
                append("Facility Types : ").append(this.getFacilityType()).append("\n").
                append("Facility : ").append(this.getFacility()).append("\n");
        return filtersValue.toString();


    }


}
