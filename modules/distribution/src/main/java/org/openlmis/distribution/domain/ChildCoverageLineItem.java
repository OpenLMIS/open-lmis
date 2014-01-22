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
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;

import static java.lang.Math.round;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildCoverageLineItem extends BaseModel {

  private Long facilityVisitId;
  private String vaccination;
  private Integer targetGroup;

  public ChildCoverageLineItem(FacilityVisit facilityVisit, Facility facility, VaccinationProduct vaccinationProduct) {
    this.facilityVisitId = facilityVisit.getId();
    this.targetGroup = calculateTargetGroup(facility.getWhoRatioFor(vaccinationProduct.getProductCode()),
      facility.getCatchmentPopulation());
    this.vaccination = vaccinationProduct.getVaccination();
  }

  private Integer calculateTargetGroup(Double whoRatio, Long catchmentPopulation) {
    Integer targetGroup = null;
    if (whoRatio != null && catchmentPopulation != null) {
      targetGroup = (int) round(catchmentPopulation * whoRatio / 100);
    }
    return targetGroup;
  }
}
