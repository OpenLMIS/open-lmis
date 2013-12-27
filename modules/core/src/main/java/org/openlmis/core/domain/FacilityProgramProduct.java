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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

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
    if (this.isActive() && this.getProduct().getActive()) {
      return this.getProduct().getProductGroup();
    }
    return null;
  }

  public Integer calculateIsa(Long population) {
    if (this.getOverriddenIsa() != null)
      return this.getOverriddenIsa();
    if (this.getProgramProductIsa() == null || population == null)
      return null;

    return this.getProgramProductIsa().calculate(population);
  }
}
