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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryReport implements ReportData {

    private String productCode;
    private String description;
    private Integer unitSize;
    private Integer unitQuantity;
    private Integer packQuantity;
    private Integer discrepancy;
    private String facilityName;
    private String facilityCode;
    private String region;


}
