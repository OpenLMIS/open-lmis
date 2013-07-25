package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyStatusReport implements ReportData {

    private String code;
    private String category;
    private String product;
    private String facility;
    private String facilityType;
    private String supplyingFacility;
    private int openingBalance;
    private int receipts;
    private int issues;
    private int adjustments;
    private int closingBalance;
    private Double monthsOfStock;
    private Double averageMonthlyConsumption;
    private Double maximumStock;
    private int reorderAmount;
    private Double minMOS;
    private Double maxMOS;

}
