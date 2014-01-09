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
import org.openlmis.core.domain.FacilityProgramProduct;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class EpiInventoryLineItem extends BaseModel {

  private Long facilityVisitId;
  private Integer idealQuantity;
  private String productCode;
  private String productName;
  private Integer productDisplayOrder;

  public EpiInventoryLineItem(Long facilityVisitId, FacilityProgramProduct facilityProgramProduct, Long population, Integer numberOfMonths) {
    this.facilityVisitId = facilityVisitId;
    this.idealQuantity = facilityProgramProduct.calculateIsa(population, numberOfMonths);
    this.productName = facilityProgramProduct.getProduct().getPrimaryName();
    this.productCode = facilityProgramProduct.getProduct().getCode();
    this.productDisplayOrder = facilityProgramProduct.getProduct().getDisplayOrder();

  }
}
