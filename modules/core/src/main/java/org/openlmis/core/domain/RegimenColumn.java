package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegimenColumn extends Column {

  private Long programId;

  private String dataType;

  public RegimenColumn(Long programId, String name, String label, String dataType, Boolean visible, Long createdBy) {
    super(name, label, visible);
    this.programId = programId;
    this.dataType = dataType;
    this.createdBy = createdBy;
  }

  @Override
  public Integer getColumnWidth() {
    if (this.name.equals("remarks")) {
      return 80;
    }
    return 40;
  }
}
