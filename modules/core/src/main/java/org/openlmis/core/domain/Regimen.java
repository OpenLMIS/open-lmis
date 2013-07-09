package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Regimen extends BaseModel {

  private String name;
  private String code;
  private Long programId;
  private Boolean active;
  private RegimenCategory category;
  private Integer displayOrder;

}
