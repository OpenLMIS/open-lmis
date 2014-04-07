package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 3/17/14
 * Time: 1:49 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockOut {
    private String facilityName;
    private String facilityCode;
    private Long requisitionGroupId;
    private String product;
    private String location;
    private Integer totalStockOut;
    private Boolean suppliedInPast;
    private Double mosSuppliedInPast;

}
