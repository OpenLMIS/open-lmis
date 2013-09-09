/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;
/**
 * User: Wolde
 * Date: 5/10/13
 * Time: 2:37 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentSummaryReport implements ReportData {

    private String productDescription;
    private String category;
    private String facilityType;
    private String facilityName;
    private String supplyingFacility;
    private String adjustmentType;
    private int adjustment;

}
