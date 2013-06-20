package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegimenCategory extends BaseModel {

  private String name;
  private String code;
  private Integer displayOrder;

}
