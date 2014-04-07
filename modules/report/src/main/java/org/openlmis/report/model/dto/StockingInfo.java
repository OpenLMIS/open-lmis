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
    private String stocking;
    private Integer stockingStat;
    private Long productId;
    private Long periodId;
    private Long programId;
    private Long rgroupId;
    private Long facilityId;
    private Integer adequatelyStocked;
    private Integer overStocked;
    private Integer stockedOut;
    private Integer understocked;
}
