package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

@Data
@NoArgsConstructor
public class ProgramProductCost implements Importable {

  @ImportFields(importFields = {
      @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true),
      @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true),
      @ImportField(name = "Price per pack", type = "double", mandatory = true)
  })
  private ProgramProduct programProduct;

  @ImportField(name = "Price per dosage unit", type = "double")
  private Double pricePerDosage;
  @ImportField(name = "Funding Source", type = "String")
  private String source;

  private String modifiedBy;

  public ProgramProductCost(ProgramProduct programProduct, Double pricePerDosage, String source) {
    this.programProduct = programProduct;
    this.pricePerDosage = pricePerDosage;
    this.source = source;
  }
}
