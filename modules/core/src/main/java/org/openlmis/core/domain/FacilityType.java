package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacilityType {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private Integer levelId;
    private Integer nominalMaxMonth;
    private Double nominalEop;
    private Integer displayOrder;
    private boolean active;

  public FacilityType(String code) {
    this.code = code;
  }
}
