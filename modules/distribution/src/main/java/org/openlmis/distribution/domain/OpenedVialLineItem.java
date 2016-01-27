/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  OpenedVialLineItem represents the number of product vials consumed along with the pack size of that product
 *  for a particular facility visited.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class OpenedVialLineItem extends BaseModel {

  private Long facilityVisitId;
  private String productVialName;
  private Integer openedVials;
  private Integer packSize;

  public OpenedVialLineItem(FacilityVisit facilityVisit,
                            Facility facility,
                            ProductVial productVial,
                            String productVialName) {
    this.facilityVisitId = facilityVisit.getId();
    this.productVialName = productVialName;
    this.packSize = productVial != null ? facility.getPackSizeFor(productVial.getProductCode()) : null;
    this.createdBy = facilityVisit.getCreatedBy();
    this.modifiedBy = facilityVisit.getModifiedBy();
  }
}
