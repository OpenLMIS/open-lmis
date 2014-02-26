package org.openlmis.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoReportData {

  private long id;

  private String name;

  private String geometry;

  private float expected;

  private float total;

  private float ever;

  private float period;

}
