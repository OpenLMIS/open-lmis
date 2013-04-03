/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
// TODO : rename to FacilityTypeApprovedProduct
public class FacilityApprovedProduct extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Facility Type Code", nested = "code")
  private FacilityType facilityType;


  @ImportFields(importFields = {
      @ImportField(name = "Program Code", nested = "program.code", mandatory = true),
      @ImportField(name = "Product Code", nested = "product.code", mandatory = true)})
  private ProgramProduct programProduct;

  @ImportField(name = "Max months of stock", mandatory = true, type = "int")
  private Integer maxMonthsOfStock = 0;

  public FacilityApprovedProduct(FacilityType facilityType, ProgramProduct programProduct, Integer maxMonthsOfStock) {
    this.facilityType = facilityType;
    this.maxMonthsOfStock = maxMonthsOfStock;
    this.setProgramProduct(programProduct);
  }

  public FacilityApprovedProduct(String facilityTypeCode, ProgramProduct programProduct, Integer maxMonthsOfStock) {
    this(new FacilityType(facilityTypeCode), programProduct, maxMonthsOfStock);
  }
}
