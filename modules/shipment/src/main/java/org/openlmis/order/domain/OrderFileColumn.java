package org.openlmis.order.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
public class OrderFileColumn {
  private String dataFieldLabel;

  private int position;

  private String columnLabel;

  @Getter(PRIVATE)
  private boolean includeInOrderFile;

  public boolean includeInOrderFile() {
    return includeInOrderFile;
  }
}
