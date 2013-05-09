package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageConsumptionReport implements ReportData {

    private String productDescription;
    private Double average;
    private String product;
    private String reportingGroup;
    private String category;
    private String facilityType;
    private String facilityName;
    private String supplyingFacility;
    private Integer minMOS;     //Minimum Months of Stock
    private Integer maxMOS;     //Maximum Months of Stock

}
