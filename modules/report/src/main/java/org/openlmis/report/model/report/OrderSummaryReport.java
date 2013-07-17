package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryReport implements ReportData {

    private String productCode;
    private String description;
    private Integer unitSize;
    private Integer unitQuantity;
    private Integer packQuantity;
    private Integer discrepancy;
    private String facilityName;
    private String facilityCode;
    private String region;


}
