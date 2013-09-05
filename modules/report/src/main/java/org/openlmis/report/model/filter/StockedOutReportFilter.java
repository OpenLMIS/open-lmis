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

    @Override
    public String toString(){

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);

        StringBuilder filtersValue = new StringBuilder("");

        String strProgram = this.getProgram() == null ? "All Programs" : this.getProgram();
        String strReportingGroup = this.getRgroup() == null ? "All Requisition Groups" : this.getRgroup();
        String strProductCategory = this.getProductCategory() == null ? "All Product Categories" : this.getProductCategory();
        String strProduct = this.getProduct() == null ? "All Products" : this.getProduct();
        String strFacilityType = this.getFacilityType() == null ? "All Facility Types" : this.getFacilityType();
        String strFacility = this.getFacility() == null || this.getFacility() =="" ? "All Facilities" : this.getFacility();

        filtersValue.
                append("Period : ").append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                append("Program : ").append(strProgram).append("\n").
                append("Reporting Groups : ").append(strReportingGroup).append("\n").
                append("Product Category : ").append(strProductCategory).append("\n").
                append("Product : ").append(strProduct).append("\n").
                append("Facility Types : ").append(strFacilityType).append("\n").
                append("Facility : ").append(strFacility).append("\n");
        return filtersValue.toString();
    }


}
