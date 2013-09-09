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
 * User: Wolde
 * Date: 5/10/13
 * Time: 2:56 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentSummaryReportFilter implements ReportData {

    private int userId;

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
    private int zoneId;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private String rgroup;
    private int programId;
    private String  adjustmentTypeId;
    private String adjustmentType;


    @Override
    public String toString(){
         if(this.getStartDate() != null && this.getEndDate() != null){
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
            StringBuilder filtersValue = new StringBuilder("");
            filtersValue.append("Period : ").append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                    append("Facility Types : ").append(this.getFacilityType()).append("\n").
                    append("Adjustment Types : ").append(this.getAdjustmentType()).append("\n").
                    append("Reporting Groups : ").append(this.getRgroup());

            return filtersValue.toString();
         }   else{
           return "No filters selected";
         }
    }
}
