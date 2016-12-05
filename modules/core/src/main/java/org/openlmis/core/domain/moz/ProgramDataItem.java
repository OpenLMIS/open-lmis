package org.openlmis.core.domain.moz;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
public class ProgramDataItem extends BaseModel {
  private ProgramDataForm programDataForm;
  private String name;
  private ProgramDataColumn programDataColumn;
  private Long value;
}
