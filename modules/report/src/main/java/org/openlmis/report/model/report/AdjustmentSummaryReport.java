package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;
/**
 * User: Wolde
 * Date: 5/10/13
 * Time: 2:37 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentSummaryReport implements ReportData {

    private String productDescription;
    private String category;
    private String facilityType;
    private String facilityName;
    private String supplyingFacility;
    private String adjustmentType;
    private Double adjustment;

}
