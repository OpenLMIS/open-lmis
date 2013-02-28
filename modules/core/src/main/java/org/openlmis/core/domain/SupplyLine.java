package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class SupplyLine implements Importable {

  Integer id;

  @ImportField(mandatory = true, name = "Supervising Node", nested = "code")
  SupervisoryNode supervisoryNode;

  @ImportField(name = "Description")
  String description;

  @ImportField(mandatory = true, name = "Program", nested = "code")
  Program program;

  @ImportField(mandatory = true, name = "Facility", nested = "code")
  Facility supplyingFacility;

  private Date modifiedDate;
  private Integer modifiedBy;

}
