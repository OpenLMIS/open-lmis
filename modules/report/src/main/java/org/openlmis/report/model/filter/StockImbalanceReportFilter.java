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


/**
 * User: Wolde
 * Date: 7/29/13
 * Time: 6:51 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockImbalanceReportFilter implements ReportData {

    private int facilityTypeId;
    private String facilityType;
    private int productId;
    private String product;
    private int productCategoryId;
    private String productCategory;
    private int rgroupId;
    private String rgroup;
    private String facility;
    private int programId;
    private String program;
    private int scheduleId;
    private String schedule;
    private int periodId;
    private String period;
    private Integer year;

    @Override
    public String toString(){

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Period : ").append(this.period).append("\n").
                append("Schedule : ").append(this.schedule).append("\n").
                append("Program : ").append(this.program).append("\n").
                append("Product Category : ").append(this.productCategory).append("\n").
                append("Product : ").append(this.product).append("\n").
                append("Facility Types : ").append(this.getFacilityType()).append("\n").
                append("Reporting Groups : ").append(this.getRgroup());

        return filtersValue.toString();
    }
}
