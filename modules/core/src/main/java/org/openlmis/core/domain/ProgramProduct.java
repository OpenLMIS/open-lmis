/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_NULL)
public class ProgramProduct extends BaseModel implements Importable {

  @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
  private Program program;
  @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
  private Product product;
  @ImportField(name = "Doses Per Month", type = "int", mandatory = true)
  private Integer dosesPerMonth;
  @ImportField(name = "Is Active", type = "boolean", mandatory = true)
  private boolean active;

  ProgramProductISA programProductIsa;

  private Money currentPrice;

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
    if (currentPrice.isNegative()) throw new DataException("programProduct.invalid.current.price");
  }

  public ProgramProduct(ProgramProduct programProduct) {
    this.id = programProduct.id;
    this.program = programProduct.program;
    this.product = programProduct.product;
    this.dosesPerMonth = programProduct.dosesPerMonth;
    this.active = programProduct.active;
    this.currentPrice = programProduct.currentPrice;
    this.programProductIsa = programProduct.programProductIsa;
  }
}
