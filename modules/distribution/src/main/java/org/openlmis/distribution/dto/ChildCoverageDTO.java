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

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.distribution.domain.ChildCoverageLineItem;
import org.openlmis.distribution.domain.OpenedVialLineItem;
import org.openlmis.distribution.domain.VaccinationChildCoverage;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  This DTO represents a container for the list of ChildCoverageLineItemDTO and list of OpenedVialLineItemDTO.
 */

@Data
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCoverageDTO extends BaseModel {

  private List<ChildCoverageLineItemDTO> childCoverageLineItems = new ArrayList<>();
  private List<OpenedVialLineItemDTO> openedVialLineItems = new ArrayList<>();

  public VaccinationChildCoverage transform() {
    VaccinationChildCoverage childCoverage = new VaccinationChildCoverage();
    List<ChildCoverageLineItem> childCoverageLineItems = new ArrayList<>();
    for (ChildCoverageLineItemDTO lineItemDTO : this.childCoverageLineItems) {
      lineItemDTO.setModifiedBy(this.modifiedBy);
      childCoverageLineItems.add(lineItemDTO.transform());
    }

    List<OpenedVialLineItem> openedVialLineItems = new ArrayList<>();
    for (OpenedVialLineItemDTO lineItemDTO : this.openedVialLineItems) {
      lineItemDTO.setModifiedBy(this.modifiedBy);
      openedVialLineItems.add(lineItemDTO.transform());
    }

    childCoverage.setChildCoverageLineItems(childCoverageLineItems);
    childCoverage.setOpenedVialLineItems(openedVialLineItems);
    return childCoverage;
  }
}
