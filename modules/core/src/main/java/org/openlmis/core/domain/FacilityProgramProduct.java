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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * FacilityProgramProduct represents product supported by given facility for a given program. This mapping is used by distribution module
 * to identify products supported by a facility and overriddenISA for that product in that facility.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class FacilityProgramProduct extends ProgramProduct {

  Long facilityId;

  ISA overriddenIsa;

  public FacilityProgramProduct(ProgramProduct programProduct, Long facilityId)
  {
    this(programProduct, facilityId, null);
  }

  public FacilityProgramProduct(ProgramProduct programProduct, Long facilityId, ISA isa)
  {
    super(programProduct);
    this.facilityId = facilityId;
    this.overriddenIsa = isa;
  }

  @JsonIgnore
  public ProductGroup getActiveProductGroup() {
    if (this.getActive() && this.getProduct().getActive()) {
      return this.getProduct().getProductGroup();
    }
    return null;
  }

  public Integer calculateIsa(Long population, Integer numberOfMonthsInPeriod)
  {
    if(population == null)
      return null;

    Integer idealQuantity;
    if (this.overriddenIsa != null)
      idealQuantity = this.overriddenIsa.calculate(population);
    else if (this.programProductIsa != null)
      idealQuantity = this.programProductIsa.calculate(population);
    else
      return null;

    idealQuantity = Math.round(idealQuantity * ((float) numberOfMonthsInPeriod / this.getProduct().getPackSize()));
    return idealQuantity < 0 ? 0 : idealQuantity;
  }


  @JsonIgnore
  public Double getWhoRatio()
  {
    if(this.overriddenIsa != null)
      return overriddenIsa.getWhoRatio();
    else if(this.programProductIsa != null)
      return programProductIsa.getWhoRatio();
    else
      return null;
  }

  public static List<FacilityProgramProduct> filterActiveProducts(List<FacilityProgramProduct> programProducts) {
    List<FacilityProgramProduct> activeProgramProducts = (List<FacilityProgramProduct>) CollectionUtils.select(programProducts, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        FacilityProgramProduct programProduct = (FacilityProgramProduct) o;
        return programProduct.getActive() && programProduct.getProduct().getActive();
      }
    });
    return activeProgramProducts;
  }
}
