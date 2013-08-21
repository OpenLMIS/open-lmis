package org.openlmis.core.domain;


import lombok.Data;

@Data
public class Configuration extends BaseModel{

  private String orderFilePrefix;
  private String orderDatePattern;
  private String periodDatePattern;
  private Boolean headerInOrderFile;

}
