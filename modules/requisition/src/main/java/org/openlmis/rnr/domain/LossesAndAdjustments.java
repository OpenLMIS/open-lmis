package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LossesAndAdjustments {

    private Integer id;
    private LossesAndAdjustmentType type;
    private Integer quantity;

}
