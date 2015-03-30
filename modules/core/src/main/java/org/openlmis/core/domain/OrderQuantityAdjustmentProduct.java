package org.openlmis.core.domain;


import groovy.transform.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderQuantityAdjustmentProduct extends BaseModel {

    private Facility facility;
    private Product product;
    private OrderQuantityAdjustmentType adjustmentType;
    private OrderQuantityAdjustmentFactor adjustmentFactor;
    private Date startDate;
    private Date endDate;
    private Long minMOS;
    private Long maxMOS;
    private String formula;


}
