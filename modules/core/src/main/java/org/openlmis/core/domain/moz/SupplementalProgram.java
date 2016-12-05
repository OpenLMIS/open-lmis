package org.openlmis.core.domain.moz;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
public class SupplementalProgram extends BaseModel {

  private String code;
  private String name;
  private String description;
  private boolean active;
}
