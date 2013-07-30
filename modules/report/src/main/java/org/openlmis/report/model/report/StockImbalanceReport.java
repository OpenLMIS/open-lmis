package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:41 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockImbalanceReport implements ReportData {
    private String facility;
    private String product;
    private Integer physicalCount;
    private Integer amc;
    private Integer months;
    private Integer orderQuantity;
    private String status;
    private String supplyingFacility;
}
