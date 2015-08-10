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

package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportParameter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class OrderReportParam
  extends BaseParam implements ReportParameter {

  private Long orderId;

  private int facilityTypeId;
  private String facilityType;
  private String facility;
  private Long facilityId;
  private int productId;
  private String product;
  private int productCategoryId;
  private String productCategory;
  private Long programId;
  private String program;
  private String orderType;
  private int zoneId;
  private Long periodId;
  private String period;
  private int scheduleId;
  private String schedule;
  private String year;

  @Override
  public String toString() {

    StringBuilder filtersValue = new StringBuilder("");
    filtersValue.append("Program: ").append(this.program).append("\n").
      append("Period: ").append(this.period).append(" - ").append(this.year).append("\n");

    return filtersValue.toString();
  }


}
