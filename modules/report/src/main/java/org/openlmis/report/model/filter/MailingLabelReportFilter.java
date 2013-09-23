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
 * User: wolde
 * Date: 3/29/13
 * Time: 7:17 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailingLabelReportFilter implements ReportData {

    //top filters
    private int facilityTypeId;
    private String facilityType;
    private int rgroupId;
    private String rgroup;

    @Override
    public String toString(){
        if(this == null ) return null;
        StringBuilder filterDescription = new StringBuilder("");
        filterDescription.append("Facility Type : ").append(facilityTypeId != 0 ? facilityType : "All Facility Types ").append("\n").
                         append("Requisition Group : ").append(rgroupId != 0 ? rgroup : "All Requisition Groups");

        return filterDescription.toString().isEmpty() ? null : filterDescription.toString();
    }

}


