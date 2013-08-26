/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryReport implements ReportData {
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "product_description")
    private String description;
    @Column(name = "packstoship")
    private Integer unitSize;
    @Column(name = "packstoship")
    private Integer unitQuantity;
    @Column(name = "packsize")
    private Integer packQuantity;
    @Column(name = "requisition_line_item_losses_adjustments.quantity")
    private Integer discrepancy;
    @Column(name = "facility_name")
    private String facilityName;
    @Column(name = "facility_code")
    private String facilityCode;
    private String region;


}
