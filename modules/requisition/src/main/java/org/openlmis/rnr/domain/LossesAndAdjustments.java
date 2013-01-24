package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LossesAndAdjustments {

    private Integer id;
    private LossesAndAdjustmentsType type;
    private Integer quantity;

}
