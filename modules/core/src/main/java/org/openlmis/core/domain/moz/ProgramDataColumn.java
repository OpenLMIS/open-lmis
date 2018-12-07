package org.openlmis.core.domain.moz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDataColumn extends BaseModel {
  private String code;
  private String label;
  private String description;
  private SupplementalProgram supplementalProgram;

  public ProgramDataColumn(String code, String label, String description) {
    this.code = code;
    this.label = label;
    this.description = description;
  }
}
