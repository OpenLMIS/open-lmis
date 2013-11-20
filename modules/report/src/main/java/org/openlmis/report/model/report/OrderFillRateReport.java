package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;


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

    @Column( name = "facility")
    private String facility;
    @Column(name = "approved")
    private Integer approved;
    @Column(name="receipts")
    private Integer receipts;
    @Column( name = "productcode")
    private String productcode;
    @Column( name = "product")
    private String product;
    @Column(name= "err_qty_received")
    private Integer err_qty_received;
    private Integer item_fill_rate;
    private Integer ORDER_FILL_RATE;
    @Column(name="facilitytype")
    private String facilityType;
    @Column(name="supplyingfacility")
    private String supplyingFacility ;
    @Column(name="category")
    private String category;
}
