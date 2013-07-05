package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegimenColumn extends BaseModel {

  private Long programId;

  private String name;

  private String label;

  private String dataType;

  private Boolean visible;

  public RegimenColumn (Long programId, String name, String label, String dataType, Boolean visible, Long createdBy) {
    this(programId, name, label, dataType, visible);
    this.createdBy = createdBy;
  }

}
