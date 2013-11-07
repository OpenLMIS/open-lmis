package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;



/**
 * Created with IntelliJ IDEA.
 * User: Hassan
 * Date: 10/28/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderFillRateReport implements ReportData {

    private String facility;
    private String product;
    private String supplyingFacility;
    private Integer approved;
    private  Integer receipts;
    private String facilityType;
    private Integer item_fill_rate;
    private String productcode;


}
