package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegimenColumn extends BaseModel {

  private String name;

  private String label;

  private Boolean visible;

  private String dataType;

  private Long programId;

}
