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

import lombok.*;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

import static java.lang.Math.ceil;

/**
 * CoverageLineItem is a base class holding facilityVisitId and targetGroup. TargetGroup is calculated on basis of
 * target population percentage and catchment population of the corresponding facility.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoverageLineItem extends BaseModel {

  protected Long facilityVisitId;
  protected Integer targetGroup;

  public CoverageLineItem(FacilityVisit facilityVisit, Facility facility, TargetGroupProduct targetGroupProduct, Integer processingPeriodMonths) {
    this.facilityVisitId = facilityVisit.getId();
    this.targetGroup = targetGroupProduct != null ? calculateTargetGroup(facility.getWhoRatioFor(targetGroupProduct.getProductCode()),
      facility.getCatchmentPopulation(), processingPeriodMonths) : null;
    this.createdBy = facilityVisit.getCreatedBy();
    this.modifiedBy = facilityVisit.getModifiedBy();
  }

  protected Integer calculateTargetGroup(Double whoRatio, Long catchmentPopulation, Integer processingPeriodMonths) {
    Integer targetGroup = null;
    if (whoRatio != null && catchmentPopulation != null) {
      targetGroup = (int) ceil((catchmentPopulation * whoRatio * processingPeriodMonths) / (100 * 12));
    }
    return targetGroup;
  }
}
