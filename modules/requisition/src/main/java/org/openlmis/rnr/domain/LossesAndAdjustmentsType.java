package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LossesAndAdjustmentsType {

    private String name;
    private String description;
    private Boolean additive;
    private Integer displayOrder;

}
