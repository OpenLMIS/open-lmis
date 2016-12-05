package org.openlmis.core.domain.moz;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;


@Data
@NoArgsConstructor
public class ProgramDataColumn extends BaseModel {
  private String code;
  private String label;
  private String description;
  private SupplementalProgram supplementalProgram;

}
