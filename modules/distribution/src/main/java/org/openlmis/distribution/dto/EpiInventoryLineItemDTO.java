/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */


package org.openlmis.distribution.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.EpiInventoryLineItem;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  DTO for EpiInventoryLineItem. It contains facilityVisitId and
 *  client side representation of EpiInventoryLineItem attributes.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class EpiInventoryLineItemDTO extends BaseModel {

  private Long facilityVisitId;
  private Reading existingQuantity;
  private Reading spoiledQuantity;
  private Integer deliveredQuantity;

  public EpiInventoryLineItem transform() {
    EpiInventoryLineItem lineItem = new EpiInventoryLineItem(this.facilityVisitId,
      this.existingQuantity.parsePositiveInt(),
      this.spoiledQuantity.parsePositiveInt(),
      this.deliveredQuantity);

    lineItem.setId(this.id);
    lineItem.setModifiedBy(this.modifiedBy);
    return lineItem;
  }
}
