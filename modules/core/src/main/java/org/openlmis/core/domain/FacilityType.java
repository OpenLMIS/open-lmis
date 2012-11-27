package org.openlmis.core.domain;

import lombok.Data;

@Data
public class FacilityType {
    private String code;
    private String name;
    private String description;
    private Integer levelId;
    private Integer nominalMaxMonth;
    private Double nominalEop;
    private Integer displayOrder;
    private boolean active;
}
