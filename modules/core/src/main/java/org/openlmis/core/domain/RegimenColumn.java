package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegimenColumn extends BaseModel {

  private String name;

  private String label;

  private Boolean visible;

  private String dataType;

  private Long programId;

  public RegimenColumn (Long programId, String name, String label, String dataType, Boolean visible) {
    this.programId = programId;
    this.name = name;
    this.label = label;
    this.dataType = dataType;
    this.visible = visible;
  }

}
