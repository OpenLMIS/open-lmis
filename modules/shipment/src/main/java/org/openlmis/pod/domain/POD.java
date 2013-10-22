package org.openlmis.pod.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POD extends BaseModel {

  private Long orderId;
  private List<PODLineItem> podLineItems;

  public void validate() {
    if (orderId == null || podLineItems == null) {
      throw new DataException("error.restapi.mandatory.missing");
    }
    if (podLineItems == null) {
      for (PODLineItem lineItem : podLineItems) {
        if (StringUtils.isEmpty(lineItem.getProductCode()) || lineItem.getQuantityReceived() == null) {
          throw new DataException("error.restapi.mandatory.missing");
        }
      }
    }
  }
}
