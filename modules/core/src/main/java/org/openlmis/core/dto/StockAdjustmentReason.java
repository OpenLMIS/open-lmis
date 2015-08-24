package org.openlmis.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@EqualsAndHashCode(callSuper=false)
public class StockAdjustmentReason extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Reason Name")
  String name;

  @ImportField(name = "Description")
  String description;

  @ImportField(mandatory = true, name = "Additive")
  Boolean additive;

  @ImportField(mandatory = true, type = "int", name = "Display Order")
  Integer displayOrder;

  @ImportField(name = "Is Default")
  Boolean isDefault;
}
