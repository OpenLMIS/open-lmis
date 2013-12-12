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
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpiUseDTO extends BaseModel {

  Long facilityId;
  Long distributionId;
  List<EpiUseLineItemDTO> lineItems;

  public EpiUse transform() {
    List<EpiUseLineItem> epiUseLineItems = new ArrayList<>();

    for (EpiUseLineItemDTO epiUseLineItemDTO : this.lineItems) {
      epiUseLineItems.add(epiUseLineItemDTO.transform());
    }

    return new EpiUse(facilityId, distributionId, epiUseLineItems);
  }
}
