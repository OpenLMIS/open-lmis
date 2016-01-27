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
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;
import org.openlmis.upload.annotation.ImportFields;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * FacilityTypeApprovedProduct represents the product approved by the facility type for a particular program. Also defines contract for upload of this
 * mapping. Facility type, program code, product code and maximum months of stock that can be stocked for this product by
 * the facility type is mandatory for upload of such mapping.
 */
@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacilityTypeApprovedProduct extends BaseModel implements Importable {

  @ImportField(mandatory = true, name = "Facility Type Code", nested = "code")
  private FacilityType facilityType;

  @ImportFields(importFields = {
    @ImportField(name = "Program Code", nested = "program.code", mandatory = true),
    @ImportField(name = "Product Code", nested = "product.code", mandatory = true)})
  private ProgramProduct programProduct;

  @ImportField(name = "Max months of stock", mandatory = true, type = "double")
  private Double maxMonthsOfStock;

  @ImportField(name = "Min months of stock", type = "double")
  private Double minMonthsOfStock;

  @ImportField(name = "Emergency order point", type = "double")
  private Double eop;

  public FacilityTypeApprovedProduct(FacilityType facilityType,
                                     ProgramProduct programProduct,
                                     Double maxMonthsOfStock) {
    this.facilityType = facilityType;
    this.maxMonthsOfStock = maxMonthsOfStock;
    this.setProgramProduct(programProduct);
  }

  public FacilityTypeApprovedProduct(String facilityTypeCode, ProgramProduct programProduct, Double maxMonthsOfStock) {
    this(new FacilityType(facilityTypeCode), programProduct, maxMonthsOfStock);
  }
}
