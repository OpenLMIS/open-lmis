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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.distribution.dto.EpiInventoryLineItemDTO;
import org.openlmis.distribution.dto.Reading;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

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
  private Integer idealQuantityByPackSize;
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
    this.idealQuantityByPackSize = facilityProgramProduct.calculateIsaByPackSize(population, numberOfMonths);
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

  public EpiInventoryLineItemDTO transform() {
    EpiInventoryLineItemDTO dto = new EpiInventoryLineItemDTO();
    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setFacilityVisitId(facilityVisitId);
    dto.setIdealQuantity(idealQuantity);
    dto.setIdealQuantityByPackSize(idealQuantityByPackSize);
    dto.setExistingQuantity(new Reading(existingQuantity));
    dto.setSpoiledQuantity(new Reading(spoiledQuantity));
    dto.setDeliveredQuantity(new Reading(deliveredQuantity));
    dto.setProgramProductId(programProductId);
    dto.setProductCode(productCode);
    dto.setProductName(productName);
    dto.setProductDisplayOrder(productDisplayOrder);

    return dto;
  }
}
