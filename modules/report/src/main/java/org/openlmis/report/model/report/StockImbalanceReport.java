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
    @Column(name = "stockinhand")
    private Integer physicalCount;
    private Integer amc;
    @Column(name = "mos")
    private Integer months;
    @Column(name = "required")
    private Integer orderQuantity;
    private String status;
    private String supplyingFacility;
}
