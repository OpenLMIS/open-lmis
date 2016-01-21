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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;

import java.util.*;

/**
 *  EpiUse represents a container for list of EpiUseLineItem.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class EpiUse {

  private List<EpiUseLineItem> lineItems = new ArrayList<>();

  public EpiUse(Facility facility, FacilityVisit facilityVisit) {

    if (facility.getSupportedPrograms().size() != 0) {
      List<FacilityProgramProduct> programProducts = FacilityProgramProduct.filterActiveProducts(facility.getSupportedPrograms().get(0).getProgramProducts());
      this.populateEpiUseLineItems(programProducts, facilityVisit);
    }

    Comparator<EpiUseLineItem> productNameComparator = new ProductNameComparator();
    Collections.sort(lineItems, productNameComparator);
  }

  private void populateEpiUseLineItems(List<FacilityProgramProduct> programProducts, FacilityVisit facilityVisit) {
    Set<ProductGroup> productGroupSet = new HashSet<>();

    for (FacilityProgramProduct facilityProgramProduct : programProducts) {
      ProductGroup productGroup = facilityProgramProduct.getActiveProductGroup();
      if (productGroup != null && productGroupSet.add(productGroup)) {
        this.lineItems.add(new EpiUseLineItem(facilityVisit, productGroup));
      }
    }
  }

  /**
   *  ProductNameComparator represents a comparator for comparing code of two EpiUseLineItem.
   */

  private class ProductNameComparator implements Comparator<EpiUseLineItem> {
    @Override
    public int compare(EpiUseLineItem lineItem1, EpiUseLineItem lineItem2) {
      return lineItem1.getProductGroup().getCode().toLowerCase().compareTo(lineItem2.getProductGroup().getCode().toLowerCase());
    }
  }
}
