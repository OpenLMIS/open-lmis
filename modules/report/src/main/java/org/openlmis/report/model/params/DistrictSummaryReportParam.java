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

import java.text.DateFormat;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class DistrictSummaryReportParam
  extends BaseParam implements ReportParameter {

  private int rgroupId;
  private String rgroup;
  private int programId;
  private String program;
  private int scheduleId;
  private String schedule;
  private int periodId;
  private String period;
  private Integer year;
  private int zoneId;
  private String zone;




  @Override
  public String toString() {
    DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
   StringBuilder filtersValue = new StringBuilder("");
    filtersValue.append("Period : ").append(this.period).append("\n").
      append("Schedule : ").append(this.schedule).append("\n").
     append("Program : ").append(this.program).append("\n").
     append("Geographic Zone : ").append(this.zone).append("\n");
    return filtersValue.toString();
  }
}
