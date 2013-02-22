package org.openlmis.rnr.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
  private Rnr requisition;
  private OrderBatch orderBatch;

  public Order(Rnr requisition) {
    this.requisition = requisition;
  }
}
