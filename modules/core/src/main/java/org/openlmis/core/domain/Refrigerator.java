package org.openlmis.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refrigerator extends BaseModel{

  String brand;
  String serialNumber;
  String model;
  Long facilityId;

}
