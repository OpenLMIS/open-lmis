package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Regimen extends BaseModel {

  private String name;
  private String code;
  private Long programId;
  private Boolean active;
  private RegimenCategory category;
  private Integer displayOrder;

}
