package org.openlmis.vaccine.domain.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VitaminSupplementationLineItem extends BaseModel {

  Long reportId;
  Long vaccineVitaminId;
  Long vitaminAgeGroupId;

  String vitaminName;
  String ageGroup;

  Long displayOrder;
  Long maleValue;
  Long femaleValue;
}
