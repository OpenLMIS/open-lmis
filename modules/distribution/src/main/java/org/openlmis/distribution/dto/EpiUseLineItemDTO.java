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

import com.google.common.base.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.distribution.domain.EpiUseLineItem;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 * DTO for EpiUseLineItem. It contains facilityVisitId and
 * client side representation of EpiUseLineItem attributes.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class EpiUseLineItemDTO extends BaseModel {

  private Long facilityVisitId;
  private ProductGroup productGroup;
  private Reading stockAtFirstOfMonth;
  private Reading stockAtEndOfMonth;
  private Reading received;
  private Reading loss;
  private Reading distributed;
  private Reading expirationDate;
  private Reading numberOfStockoutDays;

  public EpiUseLineItem transform() {
    Integer stockoutDays = Reading.safeRead(numberOfStockoutDays).parsePositiveInt();
    Integer stockAtFirstOfMonth = Reading.safeRead(this.stockAtFirstOfMonth).parsePositiveInt();
    Integer stockAtEndOfMonth = Reading.safeRead(this.stockAtEndOfMonth).parsePositiveInt();
    Integer received = Reading.safeRead(this.received).parsePositiveInt();
    Integer loss = Reading.safeRead(this.loss).parsePositiveInt();
    Integer distributed = Reading.safeRead(this.distributed).parsePositiveInt();
    String effectiveValue = Reading.safeRead(this.expirationDate).getEffectiveValue();

    EpiUseLineItem epiUseLineItem = new EpiUseLineItem(
            this.facilityVisitId,
            this.productGroup,
            stockAtFirstOfMonth,
            stockAtEndOfMonth,
            received,
            loss,
            distributed,
            effectiveValue,
            stockoutDays
    );

    epiUseLineItem.setId(this.id);
    epiUseLineItem.setModifiedBy(this.modifiedBy);
    return epiUseLineItem;
  }

}
