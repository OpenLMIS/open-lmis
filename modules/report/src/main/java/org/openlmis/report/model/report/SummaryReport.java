package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryReport implements ReportData {

    private String category;
    private String product;

    private String unit;
    private String facility;
    private String supplier;
    private String reportingGroup;
    private Integer consumption;

}
