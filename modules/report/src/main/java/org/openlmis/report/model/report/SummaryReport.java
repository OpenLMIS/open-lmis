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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SummaryReport implements ReportData {

    private String code;
    private String category;
    private String product;
    private String facilityCode;
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
