package org.openlmis.pod.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PODLineItem extends BaseModel {

  private Long podId;
  private String productCode;
  private Integer quantityReceived;

}
