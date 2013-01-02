package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LossesAndAdjustmentsType {

    private LossesAndAdjustmentsTypeEnum name;
    private Boolean additive;
    private Integer displayOrder;

}
