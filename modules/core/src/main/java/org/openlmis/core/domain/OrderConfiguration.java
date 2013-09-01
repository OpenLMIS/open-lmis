package org.openlmis.core.domain;


import lombok.Data;

@Data
public class OrderConfiguration extends BaseModel{

  private String filePrefix;
  private Boolean headerInFile;

}
