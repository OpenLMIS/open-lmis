package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegimenCategory extends BaseModel {

  private String code;
  private String name;
  private Integer displayOrder;

}
