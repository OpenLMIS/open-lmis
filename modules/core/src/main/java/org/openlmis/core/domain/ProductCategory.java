package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProductCategory implements Importable {


  private Integer id;

  @ImportField(mandatory = true, name = "Category Code")
  private String code;

  @ImportField(mandatory = true, name = "Category Name")
  private String name;

  private Integer modifiedBy;

  private Date modifiedDate;

}
