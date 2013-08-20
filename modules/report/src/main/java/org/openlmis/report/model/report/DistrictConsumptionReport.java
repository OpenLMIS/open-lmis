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
 * Date: 5/24/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictConsumptionReport implements ReportData {

    private String product;
    private String level;
    private String district;
    private Double consumption;
    private Double totalPercentage;
}
