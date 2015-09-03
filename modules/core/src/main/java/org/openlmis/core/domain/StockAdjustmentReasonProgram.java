package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

@Data
@EqualsAndHashCode(callSuper=false)
public class StockAdjustmentReasonProgram extends BaseModel implements Importable {
  @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
  private Program program;

  @ImportField(name = "Reason Name", type = "String", nested = "name", mandatory = true)
  private StockAdjustmentReason reason;
}
