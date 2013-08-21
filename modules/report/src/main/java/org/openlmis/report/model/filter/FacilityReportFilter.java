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
public class FacilityReportFilter implements ReportData {

    //top filters
    private String facilityCodeId;
    private String facilityNameId;
    private int facilityTypeId;
    private int zoneId;
    private Boolean statusId;

}
