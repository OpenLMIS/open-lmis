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

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyStatusReport implements ReportData {
    private String code;
    private String category;
    @Column(name = "li_product")
    private String product;
    private String facility;
    @Column(name = "facility_type_name")
    private String facilityType;
    private String supplyingFacility;
    @Column(name = "li_beginningbalance")
    private int openingBalance;
    @Column(name = "li_quantityreceived")
    private int receipts;
    @Column(name = "li_quantitydispensed")
    private int issues;
    @Column(name = "li_totallossesandadjustments")
    private int adjustments;
    @Column(name = "li_stockinhand")
    private int closingBalance;
    @Column(name = "li_stockinhand")
    private Double monthsOfStock;
    @Column(name = "li_amc")
    private Double averageMonthlyConsumption;
    @Column(name = "fp_maxmonthsofstock")
    private Double maximumStock;
    @Column(name = "li_calculatedorderquantity")
    private int reorderAmount;
    @Column(name = "li_maxmonthsofstock")
    private Double minMOS;
    @Column(name = "li_maxmonthsofstock")
    private Double maxMOS;

}
