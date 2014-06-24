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
public class OrderFillRateReportParam
  extends BaseParam implements ReportParameter {

  private int facilityTypeId;
  private String facilityType;
  private String productcode;
  private Long productId;
  private String product;
  private Long productCategoryId;
  private String productCategory;
  private int rgroupId;
  private String rgroup;
  private String facility;
  private Long facilityId;
  private Long programId;
  private String program;
  private int scheduleId;
  private String schedule;
  private Long periodId;
  private String period;
  private Integer year;

  @Override
  public String toString() {

    StringBuilder filtersValue = new StringBuilder("");
    filtersValue.append("Period : ").append(this.period).append("\n").
      append("Schedule : ").append(this.schedule).append("\n").
      append("Program : ").append(this.program).append("\n").
      append("Product Category : ").append(this.productCategory).append("\n").
      append("Product : ").append(this.product).append("\n").
      append("Facility Types : ").append(this.getFacilityType()).append("\n").
      append("Reporting Groups : ").append(this.getRgroup());

    return filtersValue.toString();
  }
}
