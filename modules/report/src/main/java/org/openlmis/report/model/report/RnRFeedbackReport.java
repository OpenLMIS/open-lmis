package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

/**
 * User: Wolde
 * Date: 8/21/13
 * Time: 3:39 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RnRFeedbackReport implements ReportData {
    private String product;
    private String facility;
    private String facilityCode;
    private Integer beginingBalance;
    private Integer receipts;
    private Integer dispenses;
    private Integer adjustments;
    private Integer physicalCount;
    private Integer adjustedAMC;
    private Integer newEOP;
    private Integer orderQauntity;
    private Integer quantitySupplied;
    private String unit;
    private Double maximumStock;
    private Double emergencyOrder;
}
