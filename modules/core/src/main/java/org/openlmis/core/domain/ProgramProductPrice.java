/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProgramProductPrice extends BaseModel implements Importable {

  @ImportFields(importFields = {
      @ImportField(name = "Program Code", type = "String", nested = "program.code", mandatory = true),
      @ImportField(name = "Product Code", type = "String", nested = "product.code", mandatory = true),
      @ImportField(name = "Price per pack", type = "BigDecimal", nested = "currentPrice", mandatory = true)
  })
  private ProgramProduct programProduct;

  @ImportField(name = "Price per dosage unit", type = "BigDecimal")
  private Money pricePerDosage;

  @ImportField(name = "Funding Source", type = "String")
  private String source;

  private Date startDate;
  private Date endDate;

  public ProgramProductPrice(ProgramProduct programProduct, Money pricePerDosage, String source) {
    this.programProduct = programProduct;
    this.pricePerDosage = pricePerDosage;
    this.source = source;
  }

  public void validate() {
    programProduct.validate();
    if (pricePerDosage.isNegative()) throw new DataException("programProductPrice.invalid.price.per.dosage");
  }
}
