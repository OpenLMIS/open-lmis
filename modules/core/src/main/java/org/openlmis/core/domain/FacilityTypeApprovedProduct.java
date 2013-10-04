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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class FacilityTypeApprovedProduct extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Facility Type Code", nested = "code")
  private FacilityType facilityType;

  @ImportFields(importFields = {
      @ImportField(name = "Program Code", nested = "program.code", mandatory = true),
      @ImportField(name = "Product Code", nested = "product.code", mandatory = true)})
  private ProgramProduct programProduct;

  @ImportField(name = "Max months of stock", mandatory = true, type = "int")
  private Integer maxMonthsOfStock = 0;

  @ImportField(name = "Min months of stock", mandatory = true, type = "int")
  private Integer minMonthsOfStock = 0;

  public FacilityTypeApprovedProduct(FacilityType facilityType, ProgramProduct programProduct, Integer maxMonthsOfStock) {
    this.facilityType = facilityType;
    this.maxMonthsOfStock = maxMonthsOfStock;
    this.setProgramProduct(programProduct);
  }

  public FacilityTypeApprovedProduct(String facilityTypeCode, ProgramProduct programProduct, Integer maxMonthsOfStock) {
    this(new FacilityType(facilityTypeCode), programProduct, maxMonthsOfStock);
  }
}
