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
public class SummaryReport implements ReportData {

    private String code;
    private String category;
    private String product;
    private String facility;
    private String facilityType;
    private String supplyingFacility;
    private int openingBalance;
    private int receipts;
    private int issues;
    private int adjustments;
    private int closingBalance;
    private Double monthsOfStock;
    private Double averageMonthlyConsumption;
    private Double maximumStock;
    private int reorderAmount;
    private Integer minMOS;
    private Integer maxMOS;

}
