package org.openlmis.core.domain;


import lombok.Data;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Data
public class Configuration {
  private String orderFilePrefix;
  private String orderDatePattern;
  private String periodDatePattern;

  @Getter(PRIVATE)
  private boolean headerInOrderFile;


  public boolean headerInOrderFile() {
    return headerInOrderFile;
  }
}
