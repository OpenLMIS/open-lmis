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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockImbalanceReport implements ReportData {
    private String facilityType;
    private String facility;
    private String districtName;
    private String zoneName;
    private String product;
    @Column(name = "stockinhand")
    private Integer physicalCount;
    private Integer amc;
    @Column(name = "mos")
    private Float months;
    @Column(name = "required")
    private Integer orderQuantity;
    private String status;
    private String supplyingFacility;
}
