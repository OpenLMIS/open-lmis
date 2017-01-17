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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.distribution.dto.EpiUseLineItemDTO;
import org.openlmis.distribution.dto.Reading;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  EpiUseLineItem represents an entity which keeps record of distribution and consumption pattern of a product group.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class EpiUseLineItem extends BaseModel {

  private Long facilityVisitId;
  private ProductGroup productGroup;
  private Integer stockAtFirstOfMonth;
  private Integer stockAtEndOfMonth;
  private Integer received;
  private Integer loss;
  private Integer distributed;
  private String expirationDate;
  private Integer numberOfStockoutDays;

  public EpiUseLineItem(FacilityVisit facilityVisit, ProductGroup productGroup) {
    this.facilityVisitId = facilityVisit.getId();
    this.productGroup = productGroup;
    this.createdBy = facilityVisit.getCreatedBy();
    this.modifiedBy = facilityVisit.getModifiedBy();
  }

  public EpiUseLineItemDTO transform() {
    EpiUseLineItemDTO dto = new EpiUseLineItemDTO();
    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setFacilityVisitId(facilityVisitId);
    dto.setProductGroup(productGroup);
    dto.setStockAtFirstOfMonth(new Reading(stockAtFirstOfMonth));
    dto.setStockAtEndOfMonth(new Reading(stockAtEndOfMonth));
    dto.setReceived(new Reading(received));
    dto.setLoss(new Reading(loss));
    dto.setDistributed(new Reading(distributed));
    dto.setExpirationDate(new Reading(expirationDate));
    dto.setNumberOfStockoutDays(new Reading(numberOfStockoutDays));

    return dto;
  }
}
