package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCEStorageCapacityReport implements ReportData {

  private String siteName;
  private String refrigeratorCapacityCurrent;
  private String refrigeratorCapacityRequired;
  private String refrigeratorCapacityGap;
  private String freezerCapacityCurrent;
  private String freezerCapacityRequired;
  private String freezerCapacityGap;
}
