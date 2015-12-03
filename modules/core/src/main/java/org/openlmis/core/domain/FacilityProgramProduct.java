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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

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

  Integer overriddenIsa;

  public FacilityProgramProduct(ProgramProduct programProduct, Long facilityId, Integer overriddenIsa) {
    super(programProduct);
    this.facilityId = facilityId;
    this.overriddenIsa = overriddenIsa;
  }

  @JsonIgnore
  public ProductGroup getActiveProductGroup() {
    if (this.getActive() && this.getProduct().getActive()) {
      return this.getProduct().getProductGroup();
    }
    return null;
  }

  /**
   * Calculates the ideal stock amount (ISA) the facility should be stocked to
   * with the associated product.
   * @param population the population of the facility that will be served by the
   *   product's stock.
   * @param numberOfMonthsInPeriod the number of months the ideal stock amount
   *   will need to serve the facility.
   * @return the ideal stock amount of the associated product for the associated
   *   facility or null if the ISA is not calculable.
   */
  public Integer calculateIsa(Long population, Integer numberOfMonthsInPeriod) {
    Integer idealQuantity;
    if (this.overriddenIsa != null)
      idealQuantity = this.overriddenIsa;
    else if (this.programProductIsa == null || population == null)
      return null;
    else
      idealQuantity = this.programProductIsa.calculate(population);

    idealQuantity = idealQuantity * numberOfMonthsInPeriod;
    return idealQuantity < 0 ? 0 : idealQuantity;
  }

  /**
   * Calculates the ideal stock amount (ISA) in terms of pack size.  i.e. the
   * number of whole deliverable units that a facility would be stocked to for the associated
   * product.
   * @return the number of whole deliverable units of the associated product that meets or
   * exceeds the ISA or null if the ISA is not calculable.
   * @see #calculateIsa(Long, Integer)
   */
  public Integer calculateIsaByPackSize(Long population, Integer numberOfMonthsInPeriod) {
    Integer idealQuantity = calculateIsa(population, numberOfMonthsInPeriod);
    if (idealQuantity == null) return null;

    return new Double(Math.ceil( (float) idealQuantity / this.getProduct().getPackSize() )).intValue();
  }

  @JsonIgnore
  public Double getWhoRatio(String productCode) {
    ProgramProductISA programProductIsa = this.getProgramProductIsa();
    if (this.getProduct().getCode().equals(productCode) && programProductIsa != null) {
      return programProductIsa.getWhoRatio();
    }
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
