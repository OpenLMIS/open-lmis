/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class EpiUse extends BaseModel {

  private List<EpiUseLineItem> lineItems = new ArrayList<>();

  public EpiUse(Facility facility, FacilityVisit facilityVisit) {

    if (facility.getSupportedPrograms().size() != 0) {
      List<FacilityProgramProduct> programProducts = facility.getSupportedPrograms().get(0).getProgramProducts();
      this.populateEpiUseLineItems(programProducts, facilityVisit.getCreatedBy(), facilityVisit.getId());
    }
  }

  private void populateEpiUseLineItems(List<FacilityProgramProduct> programProducts, Long createdBy, Long facilityVisitId) {
    Set<ProductGroup> productGroupSet = new HashSet<>();

    for (FacilityProgramProduct facilityProgramProduct : programProducts) {
      ProductGroup productGroup = facilityProgramProduct.getActiveProductGroup();
      if (productGroup != null && productGroupSet.add(productGroup)) {
        this.lineItems.add(new EpiUseLineItem(facilityVisitId, productGroup, createdBy));
      }
    }
  }
}
