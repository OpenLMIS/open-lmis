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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.distribution.dto.EpiUseDTO;
import org.openlmis.distribution.dto.EpiUseLineItemDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  public EpiUseDTO transform() {
    EpiUseDTO dto = new EpiUseDTO();

    List<EpiUseLineItemDTO> lineItems = new ArrayList<>();
    for (EpiUseLineItem lineItem : this.lineItems) {
      lineItems.add(lineItem.transform());
    }

    dto.setLineItems(lineItems);

    return dto;
  }

  /**
   *  ProductNameComparator represents a comparator for comparing code of two EpiUseLineItem.
   */

  private class ProductNameComparator implements Comparator<EpiUseLineItem> {
    @Override
    public int compare(EpiUseLineItem lineItem1, EpiUseLineItem lineItem2) {
      Long displayOrder1 = lineItem1.getProductGroup().getDisplayOrder();
      Long displayOrder2 = lineItem2.getProductGroup().getDisplayOrder();

      if (null == displayOrder1 && null == displayOrder2) {
        return String.CASE_INSENSITIVE_ORDER.compare(lineItem1.getProductGroup().getCode(), lineItem2.getProductGroup().getCode());
      }

      if (null == displayOrder1) {
        return 1;
      }

      if (null == displayOrder2) {
        return -1;
      }

      return Long.compare(displayOrder1, displayOrder2);
    }
  }
}
