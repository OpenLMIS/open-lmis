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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;

import java.util.ArrayList;
import java.util.List;

/**
 *  EpiInventory represents a container for list of EpiInventoryLineItem.
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class EpiInventory {

  private List<EpiInventoryLineItem> lineItems = new ArrayList<>();

  public EpiInventory(FacilityVisit facilityVisit, Facility facility, Distribution distribution) {

    List<FacilityProgramProduct> programProducts = FacilityProgramProduct.filterActiveProducts(facility.getSupportedPrograms().get(0).getProgramProducts());
    for (FacilityProgramProduct facilityProgramProduct : programProducts) {
      EpiInventoryLineItem lineItem = new EpiInventoryLineItem(facilityVisit.getId(), facilityProgramProduct, facility.getCatchmentPopulation(), distribution.getPeriod().getNumberOfMonths());
      lineItem.setCreatedBy(facilityVisit.getCreatedBy());
      lineItem.setModifiedBy(facilityVisit.getModifiedBy());
      lineItems.add(lineItem);
    }
  }
}
