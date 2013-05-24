package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

/**
 * User: Wolde
 * Date: 5/24/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictConsumptionReport implements ReportData {

    private String product;
    private String level;
    private String district;
    private Double consumption;
    private Double totalPercentage;
}
