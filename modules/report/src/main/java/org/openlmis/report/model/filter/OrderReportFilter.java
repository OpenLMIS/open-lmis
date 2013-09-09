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
 * Date: 8/5/13
 * Time: 6:32 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReportFilter implements ReportData {

    private int facilityTypeId;
    private String facilityType;
    private String facility;
    private int facilityId;
    private int productId;
    private String product;
    private int productCategoryId;
    private String productCategory;
    private int programId;
    private String program;
    private String orderType;
    private int zoneId;
    private int periodId;
    private String period;
    private int scheduleId;
    private String schedule;

    @Override
    public String toString(){

        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append("Facility Types : ").append(this.getFacilityType()).append("\n");

        return filtersValue.toString();
    }



}
