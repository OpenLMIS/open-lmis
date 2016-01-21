/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.model.report;

import lombok.*;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

@Getter
@Setter
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
