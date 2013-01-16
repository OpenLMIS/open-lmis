package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import java.util.Date;

@Data
@NoArgsConstructor
// TODO : rename to FacilityTypeApprovedProduct
public class FacilityApprovedProduct implements Importable {

  private Integer id;

  @ImportField(mandatory = true, name = "Facility Type Code", nested = "code")
  private FacilityType facilityType;


  @ImportFields(importFields = {
      @ImportField(name = "Program Code", nested = "program.code", mandatory = true),
      @ImportField(name = "Product Code", nested = "product.code", mandatory = true)})
  private ProgramProduct programProduct;

  @ImportField(name = "Max months of stock", mandatory = true, type = "int")
  private Integer maxMonthsOfStock = 0;

  private String modifiedBy;

  private Date modifiedDate;

  public FacilityApprovedProduct(FacilityType facilityType, ProgramProduct programProduct, Integer maxMonthsOfStock) {
    this.facilityType = facilityType;
    this.maxMonthsOfStock = maxMonthsOfStock;
    this.setProgramProduct(programProduct);
  }

  public FacilityApprovedProduct(String facilityTypeCode, ProgramProduct product, Integer maxMonthsOfStock) {
    this(new FacilityType(facilityTypeCode), product, maxMonthsOfStock);
  }
}
