/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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

/**
 * ProgramProductPrice represents price for a product under a program for a given time period.
 * Also defines the contract for creation/upload of this mapping like program code, product code, price per pack,
 * are mandatory. You can also provide price per dosage unit and funding source for this mapping.
 */
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
