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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityProgramProduct;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class EpiInventory extends BaseModel {
  private Long facilityId;
  private Long distributionId;
  private List<EpiInventoryLineItem> lineItems = new ArrayList<>();

  public EpiInventory(Facility facility, Distribution distribution) {
    this.facilityId = facility.getId();
    this.distributionId = distribution.getId();

    for (FacilityProgramProduct facilityProgramProduct : facility.getSupportedPrograms().get(0).getProgramProducts()) {
      if (facilityProgramProduct.isActive() && facilityProgramProduct.getProduct().getActive()) {
        lineItems.add(new EpiInventoryLineItem(facilityProgramProduct, facility.getCatchmentPopulation(), distribution.getPeriod().getNumberOfMonths()));
      }
    }
  }
}
