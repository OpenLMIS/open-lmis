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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.FacilityProgramProduct;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  EpiInventoryLineItem represents an entity which keeps record of delivery and stock information of a product.
 */

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class EpiInventoryLineItem extends BaseModel {

  private Long facilityVisitId;
  private Integer idealQuantity;
  private Integer existingQuantity;
  private Integer spoiledQuantity;
  private Integer deliveredQuantity;
  private Long programProductId;
  private String productCode;
  private String productName;
  private Integer productDisplayOrder;

  public EpiInventoryLineItem(Long facilityVisitId, FacilityProgramProduct facilityProgramProduct, Long population, Integer numberOfMonths) {
    this.facilityVisitId = facilityVisitId;
    this.programProductId = facilityProgramProduct.getId();
    this.idealQuantity = facilityProgramProduct.calculateIsa(population, numberOfMonths);
    this.productName = facilityProgramProduct.getProduct().getPrimaryName();
    this.productCode = facilityProgramProduct.getProduct().getCode();
    this.productDisplayOrder = facilityProgramProduct.getDisplayOrder();
  }

  public EpiInventoryLineItem(Long facilityVisitId, Integer existingQuantity, Integer spoiledQuantity, Integer deliveredQuantity) {
    this.facilityVisitId = facilityVisitId;
    this.existingQuantity = existingQuantity;
    this.spoiledQuantity = spoiledQuantity;
    this.deliveredQuantity = deliveredQuantity;
  }
}
