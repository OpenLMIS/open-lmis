package org.openlmis.core.domain;

import lombok.Data;

@Data
public class FacilityOperator {
    private Long id;
    private String code;
    private String text;
    private Integer displayOrder;

}
