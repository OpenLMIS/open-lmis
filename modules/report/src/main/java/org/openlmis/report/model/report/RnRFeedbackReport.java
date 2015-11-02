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
@AllArgsConstructor
@NoArgsConstructor
public class RnRFeedbackReport implements ReportData {
    private String product;
    private String productCode;
    @Column( name = "facility_name")
    private String facility;
    @Column(name = "facility_code")
    private String facilityCode;
    private Integer beginningBalance;
    @Column(name = "quantityreceived")
    private Integer totalQuantityReceived;
    @Column(name = "quantitydispensed")
    private Integer totalQuantityDispensed;
    @Column(name = "totallossesandadjustments")
    private Integer adjustments;
    @Column(name = "stockinhand")
    private Integer physicalCount;
    @Column(name = "stockoutdays")
    private Integer adjustedAMC;
    private Integer newEOP;
    @Column(name = "quantityrequested")
    private Integer orderQuantity;
    private Integer quantitySupplied;
    private String unit;
    private Double maximumStock;
    private Double emergencyOrder;
    private Integer err_open_balance;
    private Integer err_qty_required;
    private Integer err_qty_received;
    private Integer err_qty_stockinhand;
    @Column( name = "productcode")
    private String productCodeMain;
    @Column( name = "product")
    private String productMain;
    @Column( name = " quantity_shipped_total")
    private Integer totalQuantityShipped;
    private Integer productIndex;

}
