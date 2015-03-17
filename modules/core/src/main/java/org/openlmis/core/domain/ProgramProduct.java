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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * ProgramProduct represents a product available under a program and program-product specific attributes like currentPrice and dosesPerMonth.
 * Also defines contract for upload of a ProgramProduct mapping.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class ProgramProduct extends BaseModel implements Importable {

  @ImportField(name = "Program Code", type = "String", nested = "code", mandatory = true)
  private Program program;

  @ImportField(name = "Product Code", type = "String", nested = "code", mandatory = true)
  private Product product;

  @ImportField(name = "Doses Per Month", type = "int", mandatory = true)
  private Integer dosesPerMonth;

  @ImportField(name = "Is Active", type = "boolean", mandatory = true)
  private Boolean active;

  @ImportField(mandatory = true, type = "String", name = "Product Category", nested = "code")
  private ProductCategory productCategory;

  private Long productCategoryId;

  @ImportField(name = "Full Supply", type = "boolean", mandatory = true)
  private boolean fullSupply;

  @ImportField(name = "Display Order", type = "int")
  private Integer displayOrder;

  ProgramProductISA programProductIsa;

  private Money currentPrice;

  public ProgramProduct(Long id) {
    super(id);
  }

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
    this.displayOrder = programProduct.displayOrder;
    this.fullSupply = programProduct.fullSupply;
  }
}
