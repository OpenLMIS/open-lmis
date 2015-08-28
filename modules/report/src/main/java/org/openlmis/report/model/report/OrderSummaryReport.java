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
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryReport implements ReportData {
  @Column(name = "product_code")
  private String productCode;

  @Column(name = "product_description")
  private String description;

  @Column(name = "packstoship")
  private Integer packsToShip;

  @Column(name = "packsize")
  private Integer packSize;

  @Column(name = "requisition_line_item_losses_adjustments.quantity")
  private Integer discrepancy;

 @Column(name = "facility_name")
  private String facilityName;

  @Column(name = "facility_code")
  private String facilityCode;

  private String region;


}
