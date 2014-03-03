package org.openlmis.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Issa
 * Date: 3/1/14
 * Time: 10:30 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockingInfo {
    private String facility;
    private Integer AMC;
    private Integer SOH;
    private Double MOC;
    private String product;
}
