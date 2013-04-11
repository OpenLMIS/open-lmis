package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionReport implements ReportData {

    private int year;
    private String periodString;

    private String facilityType;
    private String category;
    private String product;
    private String supplier;
    private String reportingGroup;
    private Integer consumption;


}
