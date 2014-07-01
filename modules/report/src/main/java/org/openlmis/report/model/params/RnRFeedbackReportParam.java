/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class RnRFeedbackReportParam
  extends BaseParam implements ReportParameter {

  private int facilityTypeId;
  private long zoneId;
  private int facilityId;
  private int productId;
  private int productCategoryId;
  private int programId;
  private int periodId;
  private int scheduleId;
  private int rgroupId;

  private String rgroup;
  private String facilityType;
  private String facility;
  private String orderType;
  private String product;
  private String program;
  private String period;
  private String schedule;

  @Override
  public String toString() {

    StringBuilder filtersValue = new StringBuilder("");
    filtersValue.append("Program : ").append(this.program).append("\n").
      append("Schedule : ").append(this.schedule).append("\n").
      append("Period : ").append(this.period).append("\n").
      append("Requisition Group : ").append(this.rgroup).append("\n").
      append("Product : ").append(this.product).append("\n");
    return filtersValue.toString();
  }


}
