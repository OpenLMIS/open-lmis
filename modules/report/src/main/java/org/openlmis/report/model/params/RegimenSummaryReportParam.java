/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class RegimenSummaryReportParam
  extends BaseParam implements ReportParameter {

  private int regimenCategoryId;
  private String regimenCategory;
  private int rgroupId;
  private String rgroup;
  private String regimen;
  private Integer regimenId;
  private int programId;
  private String program;
  private int scheduleId;
  private String schedule;
  private int periodId;
  private String period;
  private Integer year;
  private Integer zoneId;
  private String zone;
  private int geographicLevelId;
  private String geographicLevel;


  @Override
  public String toString() {

    StringBuilder filtersValue = new StringBuilder("");
    filtersValue.append("Period : ").append(this.period).append("\n").
      append("Schedule : ").append(this.schedule).append("\n").
      append("Program : ").append(this.program).append("\n").
      append("Regimen Category : ").append(this.regimenCategory).append("\n").
      append("Regimen : ").append(this.regimen).append("\n").
      append("Geographic Level : ").append(this.geographicLevel).append("\n").
      append("District : ").append(this.zone).append("\n");


    return filtersValue.toString();
  }
}
