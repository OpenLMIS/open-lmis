/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.openlmis.core.domain.ProgramProduct;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * ProgramProductDTO consolidates information about a product under a specific program
 * like programCode, programName, productCode, productName, etc. to be used while displaying ProgramProduct
 * information to user, for eg. in feed.
 */
@Getter
@Setter
@JsonSerialize(include = NON_EMPTY)
public class ProgramProductDTO {

  private String programCode;
  private String programName;
  private String productCode;
  private String productName;
  private String description;
  private Integer unit;
  private String category;

  public ProgramProductDTO(ProgramProduct programProduct) {
    this.programCode = programProduct.getProgram().getCode();
    this.programName = programProduct.getProgram().getName();
    this.productCode = programProduct.getProduct().getCode();
    this.productName = programProduct.getProduct().getPrimaryName();
    this.description = programProduct.getProduct().getDescription();
    this.unit = programProduct.getProduct().getDosesPerDispensingUnit();
    this.category = (programProduct.getProductCategory() != null) ? programProduct.getProductCategory().getName() : null;
  }

}
