package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

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
    private String productCode;
    @Column( name = "facility_name")
    private String facility;
    @Column(name = "facility_code")
    private String facilityCode;
    private Integer beginningBalance;
    @Column(name = "quantityreceived")
    private Integer totalQuantityReceived;
    @Column(name = "quantitydispensed")
    private Integer totalQuantityDispensed;
    @Column(name = "totallossesandadjustments")
    private Integer adjustments;
    @Column(name = "stockinhand")
    private Integer physicalCount;
    @Column(name = "stockoutdays")
    private Integer adjustedAMC;
    private Integer newEOP;
    @Column(name = "quantityrequested")
    private Integer orderQuantity;
    private Integer quantitySupplied;
    private String unit;
    private Double maximumStock;
    private Double emergencyOrder;
}
