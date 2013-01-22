package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProgramProduct implements Importable {

  @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
  private Program program;
  @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
  private Product product;
  @ImportField(name = "Doses Per Month", type = "int", mandatory = true)
  private Integer dosesPerMonth;
  @ImportField(name = "Is Active", type = "boolean", mandatory = true)
  private boolean active;

  private Integer id;
  private Money currentPrice;
  private String modifiedBy;
  private Date modifiedDate;
  public static final String PROGRAM_PRODUCT_INVALID_CURRENT_PRICE = "programProduct.invalid.current.price";

  public ProgramProduct(Program program, Product product, Integer dosesPerMonth, Boolean active) {
    this.program = program;
    this.product = product;
    this.dosesPerMonth = dosesPerMonth;
    this.active = active;
  }

  public ProgramProduct(Program program, Product product, Integer dosesPerMonth, Boolean active, Money currentPrice) {
    this.program = program;
    this.product = product;
    this.dosesPerMonth = dosesPerMonth;
    this.active = active;
    this.currentPrice = currentPrice;
  }

  public void validate() {
    if(currentPrice.isNegative()) throw new DataException(PROGRAM_PRODUCT_INVALID_CURRENT_PRICE);
  }
}
