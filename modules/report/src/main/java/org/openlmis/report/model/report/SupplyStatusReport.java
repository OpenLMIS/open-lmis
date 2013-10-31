/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
