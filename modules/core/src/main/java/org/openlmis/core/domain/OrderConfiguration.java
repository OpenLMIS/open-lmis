package org.openlmis.core.domain;


import lombok.Data;

@Data
public class OrderConfiguration extends BaseModel{

  private String filePrefix;
  private String datePattern;
  private String periodDatePattern;
  private Boolean headerInFile;

}
