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
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 3/29/13
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailingLabelReportFilter implements ReportData {

    //top filters
    private String facilityCode;
    private String facilityName;
    private int facilityTypeId;
    private int zoneId;

    @Override
    public String toString(){
        if(this == null ) return null;
        StringBuilder filterDescription = new StringBuilder("");

        if(facilityName != null && !facilityName.isEmpty())
            filterDescription.append("Facility Name : ").append(facilityName).append("\n");
        if(facilityCode != null && !facilityCode.isEmpty())
            filterDescription.append("Facility Code : ").append(facilityCode).append("\n");
        if(facilityTypeId != 0)
           filterDescription.append("Facility Type : ").append(facilityTypeId).append("\n");

        return filterDescription.toString().isEmpty() ? null : filterDescription.toString();
    }

}


