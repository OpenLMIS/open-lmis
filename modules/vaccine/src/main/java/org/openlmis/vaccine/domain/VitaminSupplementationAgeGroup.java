package org.openlmis.vaccine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitaminSupplementationAgeGroup extends BaseModel {
  String name;
  String description;
  Integer displayOrder;
}
