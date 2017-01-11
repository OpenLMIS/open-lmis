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
import org.openlmis.core.domain.Facility;
import org.openlmis.distribution.dto.AdultCoverageLineItemDTO;
import org.openlmis.distribution.dto.Reading;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

/**
 *  AdultCoverageLineItem represents the data captured against a demographic group to determine coverage of a
 *  vaccination in that group. It extends CoverageLineItem to inherit the facilityVisitId and targetGroup for the
 *  corresponding demographic group.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonSerialize(include = NON_EMPTY)
public class AdultCoverageLineItem extends CoverageLineItem {

  private String demographicGroup;
  private Integer healthCenterTetanus1;
  private Integer outreachTetanus1;
  private Integer healthCenterTetanus2To5;
  private Integer outreachTetanus2To5;


  public AdultCoverageLineItem(FacilityVisit facilityVisit, Facility facility, TargetGroupProduct targetGroupForLineItem,
                               String demographicGroup, Integer processingPeriodMonths) {
    super(facilityVisit, facility, targetGroupForLineItem, processingPeriodMonths);
    this.demographicGroup = demographicGroup;
  }

  public AdultCoverageLineItemDTO transform() {
    AdultCoverageLineItemDTO dto = new AdultCoverageLineItemDTO();
    dto.setId(id);
    dto.setCreatedBy(createdBy);
    dto.setCreatedDate(createdDate);
    dto.setModifiedBy(modifiedBy);
    dto.setModifiedDate(modifiedDate);
    dto.setHealthCenterTetanus1(new Reading(healthCenterTetanus1));
    dto.setOutreachTetanus1(new Reading(outreachTetanus1));
    dto.setHealthCenterTetanus2To5(new Reading(healthCenterTetanus2To5));
    dto.setOutreachTetanus2To5(new Reading(outreachTetanus2To5));

    return dto;
  }
}
