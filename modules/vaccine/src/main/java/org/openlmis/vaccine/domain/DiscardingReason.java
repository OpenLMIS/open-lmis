package org.openlmis.vaccine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiscardingReason extends BaseModel{

  private String name;
  private Boolean requiresExplanation;
  private Long displayOrder;
}
