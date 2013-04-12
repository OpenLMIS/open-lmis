package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryReport implements ReportData {

    private String code;
    private String category;
    private String product;

    private String unit;
    private int openingBalance;
    private int quantityReceived;
    private int actualDispensedQuantity;
    private int adjustedDispensedQuantity;
    private int adjustedDistributedQuantity;
    private int balanceOnHand;
    private int productReportingRate;
    private int stockOutRate;

}
